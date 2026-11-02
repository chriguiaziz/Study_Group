package com.studygroup.config;

import com.studygroup.model.AppUser;
import com.studygroup.model.Category;
import com.studygroup.model.LinkResource;
import com.studygroup.model.Membership;
import com.studygroup.model.Message;
import com.studygroup.model.StudyGroup;
import com.studygroup.model.enums.MembershipRole;
import com.studygroup.model.enums.MembershipStatus;
import com.studygroup.model.enums.NotificationType;
import com.studygroup.model.enums.StudyFormat;
import com.studygroup.model.enums.StudyRhythm;
import com.studygroup.repository.AppUserRepository;
import com.studygroup.repository.CategoryRepository;
import com.studygroup.repository.LearningResourceRepository;
import com.studygroup.repository.MembershipRepository;
import com.studygroup.repository.MessageRepository;
import com.studygroup.repository.NotificationRepository;
import com.studygroup.repository.StudyGroupRepository;
import com.studygroup.service.NotificationService;
import com.studygroup.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    private static final List<String> LEGACY_CATEGORY_NAMES = List.of(
            "Algorithms",
            "Databases",
            "Artificial Intelligence",
            "Languages"
    );

    @Bean
    CommandLineRunner seedData(CategoryRepository categoryRepository,
                               AppUserRepository userRepository,
                               StudyGroupRepository groupRepository,
                               MembershipRepository membershipRepository,
                               MessageRepository messageRepository,
                               NotificationRepository notificationRepository,
                               LearningResourceRepository resourceRepository,
                               NotificationService notificationService) {
        return args -> {
            cleanupLegacyDemoData(categoryRepository, groupRepository, notificationRepository);

            Category lsgCommon = category(categoryRepository, "Licence Sciences Gestion - Tronc Commun", "Licence", "Sciences de Gestion", "Bases de gestion, economie, comptabilite, droit et methodes quantitatives.", "#0f766e");
            Category lsgSecondManagement = category(categoryRepository, "LSG 2eme annee - Sciences de Gestion", "Licence", "Sciences de Gestion", "Parcours de deuxieme annee oriente gestion generale.", "#2563eb");
            Category lsgSecondAccounting = category(categoryRepository, "LSG 2eme annee - Comptabilite", "Licence", "Comptabilite", "Parcours de deuxieme annee pour comptabilite et controle.", "#7c3aed");
            Category lsgFinance = category(categoryRepository, "LSG 3eme annee - Finance", "Licence", "Finance", "Analyse financiere, gestion de portefeuille, marche financier.", "#0ea5e9");
            Category lsgMarketing = category(categoryRepository, "LSG 3eme annee - Marketing", "Licence", "Marketing", "Marketing strategique, comportement consommateur et etudes de marche.", "#f97316");
            Category lsgManagement = category(categoryRepository, "LSG 3eme annee - Management", "Licence", "Management", "Management strategique, RH, organisation et leadership.", "#16a34a");
            Category ligBi = category(categoryRepository, "Licence Informatique Gestion - BI", "Licence", "Business Intelligence", "SQL, data warehouse, tableaux de bord et decisionnel.", "#4f46e5");

            Category masterEntrepreneurship = category(categoryRepository, "Master Entrepreneuriat", "Master", "Entrepreneuriat", "Business model, innovation, financement et pitch.", "#dc2626");
            Category masterFinancialAnalyst = category(categoryRepository, "Master Analyste Financier", "Master", "Analyse Financiere", "Diagnostic, valorisation, risque et reporting financier.", "#0891b2");
            Category masterBigData = category(categoryRepository, "Master Big Data Analytics et E-commerce", "Master", "Big Data / E-commerce", "Data analytics, e-commerce, machine learning et business digital.", "#9333ea");
            Category masterMarketing = category(categoryRepository, "Master Marketing", "Master", "Marketing", "Strategie marque, digital, CRM et recherche marketing.", "#ea580c");
            Category masterAccounting = category(categoryRepository, "Master Comptabilite", "Master", "Comptabilite", "Normes comptables, audit, fiscalite et controle.", "#6d28d9");
            Category masterManagement = category(categoryRepository, "Master Management", "Master", "Management", "Strategie, pilotage, conduite du changement et gouvernance.", "#15803d");
            Category masterFinance = category(categoryRepository, "Master Finance", "Master", "Finance", "Finance d'entreprise, marche, risque et investissement.", "#0369a1");

            AppUser lina = user(userRepository, "Lina Haddad", "lina@student.edu", "IHEC Carthage", "Licence Informatique de Gestion - BI", "I organize BI revision sessions and build clean dashboards.");
            AppUser adam = user(userRepository, "Adam Ben Salem", "adam@student.edu", "IHEC Carthage", "Licence Sciences de Gestion - Finance", "Finance student focused on analysis, Excel models and market cases.");
            AppUser maya = user(userRepository, "Maya Trabelsi", "maya@student.edu", "IHEC Carthage", "Master Marketing", "I like structured group work, case studies and presentation practice.");
            AppUser nour = user(userRepository, "Nour Gharbi", "nour@student.edu", "IHEC Carthage", "Master Big Data Analytics et E-commerce", "Big data and e-commerce projects, SQL, Python and analytics.");
            AppUser karim = user(userRepository, "Karim Mansour", "karim@student.edu", "IHEC Carthage", "Master Comptabilite", "Accounting, audit and IFRS revision partner.");

            StudyGroup introGestion = group(groupRepository, "IHEC L1 Gestion - Revision Generale", "Revision des fondamentaux: economie, comptabilite, droit et statistiques pour les etudiants de premiere annee.", "Comptabilite generale", lina, lsgCommon, StudyFormat.HYBRID, StudyRhythm.WEEKLY, "Bibliotheque IHEC - Salle 2", "https://zoom.us/j/100100100", true, BigDecimal.ZERO);
            StudyGroup gestionDeuxieme = group(groupRepository, "LSG 2 Sciences Gestion - Methodes Quantitatives", "Exercices de probabilites, statistiques, mathematiques financieres et methodologie d'examen.", "Methodes quantitatives", adam, lsgSecondManagement, StudyFormat.IN_PERSON, StudyRhythm.TWICE_A_WEEK, "IHEC Bloc B", null, true, BigDecimal.ZERO);
            StudyGroup accountingSecond = group(groupRepository, "LSG 2 Comptabilite - Ecritures et Fiscalite", "Atelier pratique pour ecritures comptables, fiscalite de base et preparation controle continu.", "Comptabilite approfondie", karim, lsgSecondAccounting, StudyFormat.HYBRID, StudyRhythm.EXAM_INTENSIVE, "Salle TD 14", "https://meet.google.com/ihec-compta", true, BigDecimal.ZERO);
            StudyGroup financeLicence = group(groupRepository, "L3 Finance - Analyse Financiere", "Ratios, bilan fonctionnel, cash-flow, diagnostic financier et mini cas IHEC.", "Analyse financiere", adam, lsgFinance, StudyFormat.ONLINE, StudyRhythm.EXAM_INTENSIVE, null, "https://zoom.us/j/300300300", false, new BigDecimal("8.00"));
            StudyGroup marketingLicence = group(groupRepository, "L3 Marketing - Etudes de Marche", "Questionnaires, segmentation, analyse concurrence et preparation presentation finale.", "Etudes de marche", maya, lsgMarketing, StudyFormat.HYBRID, StudyRhythm.PROJECT_WORKSHOP, "Lab Marketing", "https://meet.google.com/ihec-market", true, BigDecimal.ZERO);
            StudyGroup managementLicence = group(groupRepository, "L3 Management - Strategie et RH", "Cas pratiques de strategie, leadership, organisation et management des ressources humaines.", "Management strategique", lina, lsgManagement, StudyFormat.IN_PERSON, StudyRhythm.WEEKLY, "Amphi 3", null, true, BigDecimal.ZERO);
            StudyGroup biLicence = group(groupRepository, "BI Lab - SQL, ETL et Dashboards", "Groupe pour SQL avance, data warehouse, Power BI/Tableau et mini projet decisionnel.", "Data warehouse", nour, ligBi, StudyFormat.HYBRID, StudyRhythm.PROJECT_WORKSHOP, "Salle Informatique 4", "https://zoom.us/j/400400400", true, BigDecimal.ZERO);

            StudyGroup entrepreneurship = group(groupRepository, "Master Entrepreneuriat - Business Plan Studio", "Construction de business model, pitch deck, validation marche et plan financier.", "Business plan", lina, masterEntrepreneurship, StudyFormat.IN_PERSON, StudyRhythm.PROJECT_WORKSHOP, "Incubateur IHEC", null, true, BigDecimal.ZERO);
            StudyGroup analyst = group(groupRepository, "Master Analyste Financier - Valorisation", "DCF, comparables, cout du capital, risque et rapport d'analyse financier.", "Valorisation d'entreprise", adam, masterFinancialAnalyst, StudyFormat.ONLINE, StudyRhythm.EXAM_INTENSIVE, null, "https://zoom.us/j/500500500", false, new BigDecimal("12.00"));
            StudyGroup bigData = group(groupRepository, "Master Big Data - Analytics Sprint", "Python, data cleaning, e-commerce KPIs, clustering et dashboard analytics.", "Data analytics", nour, masterBigData, StudyFormat.HYBRID, StudyRhythm.PROJECT_WORKSHOP, "Data Lab IHEC", "https://meet.google.com/ihec-bigdata", true, BigDecimal.ZERO);
            StudyGroup masterMkt = group(groupRepository, "Master Marketing - Digital Strategy", "CRM, content strategy, funnels, analytics et preparation des cas marketing.", "Marketing digital", maya, masterMarketing, StudyFormat.ONLINE, StudyRhythm.WEEKLY, null, "https://zoom.us/j/600600600", true, BigDecimal.ZERO);
            StudyGroup masterCompta = group(groupRepository, "Master Comptabilite - Audit et IFRS", "Normes IFRS, audit, consolidation, controle interne et revision intensive.", "Audit et IFRS", karim, masterAccounting, StudyFormat.IN_PERSON, StudyRhythm.EXAM_INTENSIVE, "Salle Master C", null, true, BigDecimal.ZERO);
            StudyGroup masterMgmt = group(groupRepository, "Master Management - Strategy Board", "Management strategique, gouvernance, conduite du changement et cas d'entreprise.", "Strategie d'entreprise", maya, masterManagement, StudyFormat.HYBRID, StudyRhythm.WEEKEND_SESSION, "Salle Executive", "https://meet.google.com/ihec-management", true, BigDecimal.ZERO);
            StudyGroup masterFin = group(groupRepository, "Master Finance - Portfolio Workshop", "Allocation, risque, produits financiers, Excel model et simulations de portefeuille.", "Gestion de portefeuille", adam, masterFinance, StudyFormat.HYBRID, StudyRhythm.PROJECT_WORKSHOP, "Finance Lab", "https://zoom.us/j/700700700", false, new BigDecimal("10.00"));

            List<StudyGroup> groups = List.of(introGestion, gestionDeuxieme, accountingSecond, financeLicence, marketingLicence, managementLicence, biLicence, entrepreneurship, analyst, bigData, masterMkt, masterCompta, masterMgmt, masterFin);
            for (StudyGroup studyGroup : groups) {
                ensureMembership(membershipRepository, studyGroup.getOwner(), studyGroup, MembershipRole.OWNER);
            }
            ensureMembership(membershipRepository, lina, biLicence, MembershipRole.MEMBER);
            ensureMembership(membershipRepository, adam, financeLicence, MembershipRole.OWNER);
            ensureMembership(membershipRepository, maya, marketingLicence, MembershipRole.OWNER);
            ensureMembership(membershipRepository, nour, bigData, MembershipRole.OWNER);
            ensureMembership(membershipRepository, karim, masterCompta, MembershipRole.OWNER);

            seedActivity(messageRepository, resourceRepository, introGestion, lina, "Bienvenue! On commence par comptabilite et statistiques jeudi.", "Plan revision L1", "Checklist des chapitres importants pour L1.", "https://www.ihec.rnu.tn/");
            seedActivity(messageRepository, resourceRepository, biLicence, nour, "J'ai prepare un schema data warehouse simple pour le mini projet.", "BI roadmap", "SQL, ETL, data warehouse et dashboard.", "https://www.mysql.com/");
            seedActivity(messageRepository, resourceRepository, financeLicence, adam, "Apportez vos calculatrices, on corrige un cas complet d'analyse financiere.", "Analyse financiere template", "Modele de ratios et diagnostic financier.", "https://corporatefinanceinstitute.com/");
            seedActivity(messageRepository, resourceRepository, masterMkt, maya, "Prochaine session: segmentation et marketing digital.", "Marketing case toolkit", "Structure pour resoudre un cas marketing.", "https://www.hubspot.com/resources");

            boolean welcomeAlreadySent = notificationService.forUser(lina.getId()).stream()
                    .anyMatch(notification -> "Welcome to StudyGroup IHEC".equals(notification.getTitle()));
            if (!welcomeAlreadySent) {
                notificationService.notify(lina, "Welcome to StudyGroup IHEC", "Your demo account is ready. Password for seeded users is 1234.", NotificationType.ACTIVITY);
            }
        };
    }

    private Category category(CategoryRepository repository, String name, String level, String track, String description, String color) {
        return repository.findByNameIgnoreCase(name)
                .map(existing -> {
                    existing.setAcademicLevel(level);
                    existing.setTrack(track);
                    existing.setDescription(description);
                    existing.setAccentColor(color);
                    return repository.save(existing);
                })
                .orElseGet(() -> repository.save(new Category(name, level, track, description, color)));
    }

    private void cleanupLegacyDemoData(CategoryRepository categoryRepository, StudyGroupRepository groupRepository, NotificationRepository notificationRepository) {
        for (String legacyName : LEGACY_CATEGORY_NAMES) {
            categoryRepository.findByNameIgnoreCase(legacyName).ifPresent(category -> {
                groupRepository.findByCategoryIdOrderByCreatedAtDesc(category.getId())
                        .forEach(group -> {
                            notificationRepository.deleteByGroupId(group.getId());
                            groupRepository.delete(group);
                        });
                categoryRepository.delete(category);
            });
        }
    }

    private AppUser user(AppUserRepository repository, String name, String email, String university, String major, String bio) {
        return repository.findByEmailIgnoreCase(email)
                .map(existing -> {
                    existing.updateProfile(name, university, major, bio);
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    AppUser user = new AppUser(name, email, PasswordUtil.hash("1234"));
                    user.updateProfile(name, university, major, bio);
                    return repository.save(user);
                });
    }

    private StudyGroup group(StudyGroupRepository repository,
                             String name,
                             String description,
                             String subject,
                             AppUser owner,
                             Category category,
                             StudyFormat format,
                             StudyRhythm rhythm,
                             String location,
                             String meetingLink,
                             boolean free,
                             BigDecimal price) {
        return repository.findByNameIgnoreCase(name)
                .map(existing -> {
                    existing.setDescription(description);
                    existing.setOwner(owner);
                    existing.setCategory(category);
                    existing.setFormat(format);
                    existing.setRhythm(rhythm);
                    existing.setSubject(subject);
                    existing.setLocation(location);
                    existing.setMeetingLink(meetingLink);
                    existing.setFree(free);
                    existing.setPrice(price);
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    StudyGroup group = new StudyGroup(name, description, owner, category);
                    group.setFormat(format);
                    group.setRhythm(rhythm);
                    group.setSubject(subject);
                    group.setLocation(location);
                    group.setMeetingLink(meetingLink);
                    group.setFree(free);
                    group.setPrice(price);
                    group.setCapacity(24);
                    return repository.save(group);
                });
    }

    private void ensureMembership(MembershipRepository repository, AppUser user, StudyGroup group, MembershipRole role) {
        Membership membership = repository.findByUserIdAndGroupId(user.getId(), group.getId())
                .orElseGet(() -> new Membership(user, group, role));
        membership.setRole(role);
        membership.setStatus(MembershipStatus.ACTIVE);
        repository.save(membership);
    }

    private void seedActivity(MessageRepository messageRepository,
                              LearningResourceRepository resourceRepository,
                              StudyGroup group,
                              AppUser owner,
                              String message,
                              String resourceTitle,
                              String resourceDescription,
                              String url) {
        if (messageRepository.findByGroupIdOrderByCreatedAtAsc(group.getId()).isEmpty()) {
            messageRepository.save(new Message(message, owner, group));
        }
        if (resourceRepository.findByGroupIdOrderByCreatedAtDesc(group.getId()).isEmpty()) {
            resourceRepository.save(new LinkResource(resourceTitle, resourceDescription, url, owner, group));
        }
    }
}
