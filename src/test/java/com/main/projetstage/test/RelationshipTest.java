/*package com.main.projetstage.test;

import com.main.projetstage.models.ArticleRecette;
import com.main.projetstage.models.OperationRecette;
import com.main.projetstage.models.PropositionNonValeur;
import com.main.projetstage.repositories.ArticleRecetteRepository;
import com.main.projetstage.repositories.OperationRecetteRepository;
import com.main.projetstage.repositories.PropositionNonValeurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RelationshipTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArticleRecetteRepository articleRecetteRepository;

    @Autowired
    private OperationRecetteRepository operationRecetteRepository;

    @Autowired
    private PropositionNonValeurRepository propositionNonValeurRepository;

    private ArticleRecette articleRecette1;
    private ArticleRecette articleRecette2;

    @BeforeEach
    void setUp() {
        // Create some base ArticleRecette entities for testing
        // The constructor for ArticleRecette now expects an Integer for numArticleRecette (ID)
        articleRecette1 = new ArticleRecette(101F, "TypeA");
        entityManager.persist(articleRecette1);

        articleRecette2 = new ArticleRecette(102F, "TypeB");
        entityManager.persist(articleRecette2);

        entityManager.flush();
        entityManager.clear();
    }

    // --- Test for OperationRecette (Many-to-One with ArticleRecette) ---

    @Test
    @Transactional
    void testOperationRecetteCreationAndRelationship() {
        // Fetch articleRecette1 again using its ID (numArticleRecette)
        ArticleRecette fetchedArticleRecette = articleRecetteRepository.findById(articleRecette1.getNumArticleRecette()).orElseThrow();

        OperationRecette op1 = new OperationRecette(
                LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now(),
                100.0f, "Virement", "Validée", 5.0f, "Banque", "Première opération", fetchedArticleRecette
        );
        OperationRecette op2 = new OperationRecette(
                LocalDate.now(), LocalDate.now().plusDays(2), LocalDate.now(),
                150.0f, "Espèces", "En attente", 0.0f, "Caisse", "Seconde opération", fetchedArticleRecette
        );

        // Add operations to the ArticleRecette's list using helper methods (crucial)
        fetchedArticleRecette.addOperationRecette(op1);
        fetchedArticleRecette.addOperationRecette(op2);

        // Saving OperationRecette entities directly
        operationRecetteRepository.save(op1);
        operationRecetteRepository.save(op2);

        entityManager.flush();
        entityManager.clear();

        // Verify the relationship from OperationRecette side
        Optional<OperationRecette> retrievedOp1 = operationRecetteRepository.findById(op1.getNumRecette());
        assertThat(retrievedOp1).isPresent();
        assertThat(retrievedOp1.get().getArticleRecette().getNumArticleRecette()).isEqualTo(fetchedArticleRecette.getNumArticleRecette());

        // Verify the relationship from ArticleRecette side (collection)
        ArticleRecette retrievedArticle = articleRecetteRepository.findById(fetchedArticleRecette.getNumArticleRecette()).orElseThrow();
        assertThat(retrievedArticle.getOperationsRecette()).hasSize(2);
        assertThat(retrievedArticle.getOperationsRecette())
                .extracting(OperationRecette::getNumRecette)
                .containsExactlyInAnyOrder(op1.getNumRecette(), op2.getNumRecette());
    }

    @Test
    @Transactional
    void testOperationRecetteMandatoryRelationship() {
        // Try to save an OperationRecette without an ArticleRecette
        OperationRecette op = new OperationRecette(
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                50.0f, "Chèque", "Nouveau", 0.0f, "Chèque", "Test sans article", null
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            operationRecetteRepository.save(op);
            entityManager.flush();
        });
    }

    @Test
    @Transactional
    void testOperationRecetteOrphanRemoval() {
        ArticleRecette fetchedArticleRecette = articleRecetteRepository.findById(articleRecette1.getNumArticleRecette()).orElseThrow();

        OperationRecette op1 = new OperationRecette(
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                100.0f, "Test", "Test", 0.0f, "Test", "Test", fetchedArticleRecette
        );
        fetchedArticleRecette.addOperationRecette(op1);
        operationRecetteRepository.save(op1);
        entityManager.flush();

        entityManager.clear();
        ArticleRecette retrievedArticle = articleRecetteRepository.findById(fetchedArticleRecette.getNumArticleRecette()).orElseThrow();
        assertThat(retrievedArticle.getOperationsRecette()).hasSize(1);

        retrievedArticle.removeOperationRecette(retrievedArticle.getOperationsRecette().get(0));

        articleRecetteRepository.save(retrievedArticle);
        entityManager.flush();
        entityManager.clear();

        assertThat(operationRecetteRepository.findById(op1.getNumRecette())).isNotPresent();
        ArticleRecette finalArticle = articleRecetteRepository.findById(retrievedArticle.getNumArticleRecette()).orElseThrow();
        assertThat(finalArticle.getOperationsRecette()).isEmpty();
    }

    // --- Test for PropositionNonValeur (One-to-One with ArticleRecette) ---

    @Test
    @Transactional
    void testPropositionNonValeurCreationAndRelationship() {
        ArticleRecette fetchedArticleRecette = articleRecetteRepository.findById(articleRecette1.getNumArticleRecette()).orElseThrow();

        PropositionNonValeur pnv = new PropositionNonValeur(
                1, 2025, LocalDate.now(), LocalDate.now().plusDays(5),
                "En cours", 500.0f, "Client en faillite", fetchedArticleRecette
        );

        // Set the bidirectional link from ArticleRecette side (optional but good practice)
        fetchedArticleRecette.setPropositionNonValeur(pnv);

        propositionNonValeurRepository.save(pnv);
        articleRecetteRepository.save(fetchedArticleRecette); // Save ArticleRecette if cascade isn't ALL on PNV side

        entityManager.flush();
        entityManager.clear();

        Optional<PropositionNonValeur> retrievedPnv = propositionNonValeurRepository.findById(pnv.getId());
        assertThat(retrievedPnv).isPresent();
        assertThat(retrievedPnv.get().getArticleRecette().getNumArticleRecette()).isEqualTo(fetchedArticleRecette.getNumArticleRecette());

        ArticleRecette retrievedArticle = articleRecetteRepository.findById(fetchedArticleRecette.getNumArticleRecette()).orElseThrow();
        assertThat(retrievedArticle.getPropositionNonValeur()).isNotNull();
        assertThat(retrievedArticle.getPropositionNonValeur().getId()).isEqualTo(pnv.getId());
    }

    @Test
    @Transactional
    void testPropositionNonValeurMandatoryRelationship() {
        PropositionNonValeur pnv = new PropositionNonValeur(
                2, 2025, LocalDate.now(), LocalDate.now(),
                "Créée", 100.0f, "Créance douteuse", null
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            propositionNonValeurRepository.save(pnv);
            entityManager.flush();
        });
    }

    @Test
    @Transactional
    void testPropositionNonValeurUniqueRelationship() {
        ArticleRecette fetchedArticleRecette = articleRecetteRepository.findById(articleRecette1.getNumArticleRecette()).orElseThrow();

        PropositionNonValeur pnv1 = new PropositionNonValeur(
                3, 2025, LocalDate.now(), LocalDate.now(),
                "Valide", 200.0f, "Test unique 1", fetchedArticleRecette
        );
        propositionNonValeurRepository.save(pnv1);
        entityManager.flush();
        entityManager.clear();

        ArticleRecette fetchedArticleRecetteAgain = articleRecetteRepository.findById(articleRecette1.getNumArticleRecette()).orElseThrow();

        PropositionNonValeur pnv2 = new PropositionNonValeur(
                4, 2025, LocalDate.now().plusDays(1), LocalDate.now().plusDays(1),
                "Invalide", 300.0f, "Test unique 2", fetchedArticleRecetteAgain
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            propositionNonValeurRepository.save(pnv2);
            entityManager.flush();
        });
    }

    @Test
    @Transactional
    void testCascadeOnArticleRecetteDeletion() {
        ArticleRecette fetchedArticleRecette = articleRecetteRepository.findById(articleRecette1.getNumArticleRecette()).orElseThrow();

        OperationRecette op = new OperationRecette(
                LocalDate.now(), LocalDate.now(), LocalDate.now(),
                75.0f, "Test Delete", "Active", 0.0f, "Cash", "For cascade test", fetchedArticleRecette
        );
        fetchedArticleRecette.addOperationRecette(op);
        operationRecetteRepository.save(op);

        PropositionNonValeur pnv = new PropositionNonValeur(
                5, 2025, LocalDate.now(), LocalDate.now(),
                "Test PNV Delete", 123.0f, "For cascade test", fetchedArticleRecette
        );
        fetchedArticleRecette.setPropositionNonValeur(pnv);
        propositionNonValeurRepository.save(pnv);

        entityManager.flush();
        Long opId = op.getNumRecette();
        Long pnvId = pnv.getId();
        Long arId = fetchedArticleRecette.getNumArticleRecette(); // Correct type for ArticleRecette ID
        entityManager.clear();

        assertThat(articleRecetteRepository.findById(arId)).isPresent();
        assertThat(operationRecetteRepository.findById(opId)).isPresent();
        assertThat(propositionNonValeurRepository.findById(pnvId)).isPresent();

        articleRecetteRepository.deleteById(arId);
        entityManager.flush();
        entityManager.clear();

        assertThat(articleRecetteRepository.findById(arId)).isNotPresent();
        assertThat(operationRecetteRepository.findById(opId)).isNotPresent();
        assertThat(propositionNonValeurRepository.findById(pnvId)).isNotPresent();
    }
}*/