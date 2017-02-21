package org.cardiacatlas.xpacs.web.rest;

import org.cardiacatlas.xpacs.XpacswebApp;

import org.cardiacatlas.xpacs.domain.AuxFile;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.AuxFileRepository;
import org.cardiacatlas.xpacs.repository.search.AuxFileSearchRepository;
import org.cardiacatlas.xpacs.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AuxFileResource REST controller.
 *
 * @see AuxFileResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XpacswebApp.class)
public class AuxFileResourceIntTest {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_URI = "AAAAAAAAAA";
    private static final String UPDATED_URI = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private AuxFileRepository auxFileRepository;

    @Autowired
    private AuxFileSearchRepository auxFileSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAuxFileMockMvc;

    private AuxFile auxFile;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
            AuxFileResource auxFileResource = new AuxFileResource(auxFileRepository, auxFileSearchRepository);
        this.restAuxFileMockMvc = MockMvcBuilders.standaloneSetup(auxFileResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuxFile createEntity(EntityManager em) {
        AuxFile auxFile = new AuxFile()
                .creation_date(DEFAULT_CREATION_DATE)
                .filename(DEFAULT_FILENAME)
                .uri(DEFAULT_URI)
                .description(DEFAULT_DESCRIPTION);
        // Add required entity
        PatientInfo patientInfoFK = PatientInfoResourceIntTest.createEntity(em);
        em.persist(patientInfoFK);
        em.flush();
        auxFile.setPatientInfoFK(patientInfoFK);
        return auxFile;
    }

    @Before
    public void initTest() {
        auxFileSearchRepository.deleteAll();
        auxFile = createEntity(em);
    }

    @Test
    @Transactional
    public void createAuxFile() throws Exception {
        int databaseSizeBeforeCreate = auxFileRepository.findAll().size();

        // Create the AuxFile

        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isCreated());

        // Validate the AuxFile in the database
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeCreate + 1);
        AuxFile testAuxFile = auxFileList.get(auxFileList.size() - 1);
        assertThat(testAuxFile.getCreation_date()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testAuxFile.getFilename()).isEqualTo(DEFAULT_FILENAME);
        assertThat(testAuxFile.getUri()).isEqualTo(DEFAULT_URI);
        assertThat(testAuxFile.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AuxFile in Elasticsearch
        AuxFile auxFileEs = auxFileSearchRepository.findOne(testAuxFile.getId());
        assertThat(auxFileEs).isEqualToComparingFieldByField(testAuxFile);
    }

    @Test
    @Transactional
    public void createAuxFileWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = auxFileRepository.findAll().size();

        // Create the AuxFile with an existing ID
        AuxFile existingAuxFile = new AuxFile();
        existingAuxFile.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingAuxFile)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreation_dateIsRequired() throws Exception {
        int databaseSizeBeforeTest = auxFileRepository.findAll().size();
        // set the field null
        auxFile.setCreation_date(null);

        // Create the AuxFile, which fails.

        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isBadRequest());

        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFilenameIsRequired() throws Exception {
        int databaseSizeBeforeTest = auxFileRepository.findAll().size();
        // set the field null
        auxFile.setFilename(null);

        // Create the AuxFile, which fails.

        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isBadRequest());

        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUriIsRequired() throws Exception {
        int databaseSizeBeforeTest = auxFileRepository.findAll().size();
        // set the field null
        auxFile.setUri(null);

        // Create the AuxFile, which fails.

        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isBadRequest());

        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAuxFiles() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);

        // Get all the auxFileList
        restAuxFileMockMvc.perform(get("/api/aux-files?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auxFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].filename").value(hasItem(DEFAULT_FILENAME.toString())))
            .andExpect(jsonPath("$.[*].uri").value(hasItem(DEFAULT_URI.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getAuxFile() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);

        // Get the auxFile
        restAuxFileMockMvc.perform(get("/api/aux-files/{id}", auxFile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(auxFile.getId().intValue()))
            .andExpect(jsonPath("$.creation_date").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.filename").value(DEFAULT_FILENAME.toString()))
            .andExpect(jsonPath("$.uri").value(DEFAULT_URI.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAuxFile() throws Exception {
        // Get the auxFile
        restAuxFileMockMvc.perform(get("/api/aux-files/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAuxFile() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);
        auxFileSearchRepository.save(auxFile);
        int databaseSizeBeforeUpdate = auxFileRepository.findAll().size();

        // Update the auxFile
        AuxFile updatedAuxFile = auxFileRepository.findOne(auxFile.getId());
        updatedAuxFile
                .creation_date(UPDATED_CREATION_DATE)
                .filename(UPDATED_FILENAME)
                .uri(UPDATED_URI)
                .description(UPDATED_DESCRIPTION);

        restAuxFileMockMvc.perform(put("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAuxFile)))
            .andExpect(status().isOk());

        // Validate the AuxFile in the database
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeUpdate);
        AuxFile testAuxFile = auxFileList.get(auxFileList.size() - 1);
        assertThat(testAuxFile.getCreation_date()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testAuxFile.getFilename()).isEqualTo(UPDATED_FILENAME);
        assertThat(testAuxFile.getUri()).isEqualTo(UPDATED_URI);
        assertThat(testAuxFile.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AuxFile in Elasticsearch
        AuxFile auxFileEs = auxFileSearchRepository.findOne(testAuxFile.getId());
        assertThat(auxFileEs).isEqualToComparingFieldByField(testAuxFile);
    }

    @Test
    @Transactional
    public void updateNonExistingAuxFile() throws Exception {
        int databaseSizeBeforeUpdate = auxFileRepository.findAll().size();

        // Create the AuxFile

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAuxFileMockMvc.perform(put("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isCreated());

        // Validate the AuxFile in the database
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAuxFile() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);
        auxFileSearchRepository.save(auxFile);
        int databaseSizeBeforeDelete = auxFileRepository.findAll().size();

        // Get the auxFile
        restAuxFileMockMvc.perform(delete("/api/aux-files/{id}", auxFile.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean auxFileExistsInEs = auxFileSearchRepository.exists(auxFile.getId());
        assertThat(auxFileExistsInEs).isFalse();

        // Validate the database is empty
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAuxFile() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);
        auxFileSearchRepository.save(auxFile);

        // Search the auxFile
        restAuxFileMockMvc.perform(get("/api/_search/aux-files?query=id:" + auxFile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auxFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].filename").value(hasItem(DEFAULT_FILENAME.toString())))
            .andExpect(jsonPath("$.[*].uri").value(hasItem(DEFAULT_URI.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuxFile.class);
    }
}
