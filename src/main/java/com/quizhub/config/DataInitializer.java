package com.quizhub.config;

import com.quizhub.model.*;
import com.quizhub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           TopicRepository topicRepository,
                           QuestionRepository questionRepository,
                           QuizRepository quizRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
        }
        if (topicRepository.count() == 0) {
            seedTopicsAndQuestions();
            seedQuizzes();
        }
    }

    private void seedUsers() {
        User admin = new User("Prof. Eleanor Vance", "admin@quizhub.com", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN);
        User student = new User("Alex Mercer", "student@quizhub.com", passwordEncoder.encode("student123"), Role.ROLE_STUDENT);
        userRepository.saveAll(Arrays.asList(admin, student));
    }

    private void seedTopicsAndQuestions() {
        // 1. Science (OpenTDB: 17)
        Topic science = new Topic("Science", "Physics, Chemistry, Biology, and Natural Wonders", 17);
        topicRepository.save(science);

        addMcq(science, "What is the chemical symbol for Gold?", "Gold is represented by Au from the Latin aurum.", Difficulty.EASY,
                "Au", true, "Ag", false, "Fe", false, "Pb", false);
        addMcq(science, "What is the powerhouse of the cell?", "Mitochondria generate most of the cell's ATP.", Difficulty.EASY,
                "Mitochondria", true, "Nucleus", false, "Ribosome", false, "Golgi Apparatus", false);
        addTrueFalse(science, "Light travels faster in a vacuum than through glass.", "Light travels at c in vacuum and slows down inside denser media.", Difficulty.EASY, true);
        addTrueFalse(science, "Sound waves can travel through a perfect vacuum.", "Sound requires a material medium to propagate.", Difficulty.EASY, false);
        addMultiSelect(science, "Which of the following are Noble Gases?", "Helium, Neon, and Argon have complete valence electron shells.", Difficulty.MEDIUM,
                Arrays.asList("Helium", "Neon", "Argon"),
                Arrays.asList("Helium", "Neon", "Argon", "Oxygen", "Nitrogen"));
        addMcq(science, "What is the speed of light in vacuum?", "Speed of light is approximately 299,792,458 m/s.", Difficulty.MEDIUM,
                "~300,000 km/s", true, "~150,000 km/s", false, "~3,000 km/s", false, "~30,000 km/s", false);
        addMcq(science, "Which subatomic particle carries a negative charge?", "Electrons carry negative charge (-1e).", Difficulty.EASY,
                "Electron", true, "Proton", false, "Neutron", false, "Positron", false);
        addTrueFalse(science, "Absolute zero is 0 degrees Celsius.", "Absolute zero is 0 Kelvin or -273.15 Celsius.", Difficulty.MEDIUM, false);
        addMultiSelect(science, "Which of these planets are Gas Giants in our solar system?", "Jupiter and Saturn are Gas Giants; Uranus and Neptune are Ice Giants.", Difficulty.HARD,
                Arrays.asList("Jupiter", "Saturn"),
                Arrays.asList("Jupiter", "Saturn", "Mars", "Venus", "Mercury"));
        addMcq(science, "What is the primary gas found in Earth's atmosphere?", "Nitrogen makes up roughly 78% of Earth's atmosphere.", Difficulty.EASY,
                "Nitrogen", true, "Oxygen", false, "Carbon Dioxide", false, "Argon", false);

        // 2. History (OpenTDB: 23)
        Topic history = new Topic("History", "World History, Ancient Empires, Revolutions, and Eras", 23);
        topicRepository.save(history);

        addMcq(history, "In which year did World War II end?", "WWII ended in 1945 following the surrender of Axis powers.", Difficulty.EASY,
                "1945", true, "1944", false, "1939", false, "1918", false);
        addTrueFalse(history, "The Roman Empire fell in 476 AD.", "The Western Roman Empire collapsed with the deposition of Romulus Augustulus in 476 AD.", Difficulty.EASY, true);
        addMcq(history, "Who was the first President of the United States?", "George Washington served from 1789 to 1797.", Difficulty.EASY,
                "George Washington", true, "Thomas Jefferson", false, "John Adams", false, "Alexander Hamilton", false);
        addTrueFalse(history, "The Great Wall of China was built in a single decade.", "The Great Wall was constructed and rebuilt across several dynasties.", Difficulty.EASY, false);
        addMultiSelect(history, "Which of the following were Ancient Wonder of the World?", "Colossus of Rhodes and Lighthouse of Alexandria were classical wonders.", Difficulty.HARD,
                Arrays.asList("Colossus of Rhodes", "Lighthouse of Alexandria", "Hanging Gardens of Babylon"),
                Arrays.asList("Colossus of Rhodes", "Lighthouse of Alexandria", "Hanging Gardens of Babylon", "Eiffel Tower", "Taj Mahal"));
        addMcq(history, "The Magna Carta was signed in which century?", "Signed in 1215, which is the 13th Century.", Difficulty.MEDIUM,
                "13th Century", true, "11th Century", false, "15th Century", false, "17th Century", false);
        addMcq(history, "Who was the legendary queen of Ancient Egypt associated with Julius Caesar and Mark Antony?", "Cleopatra VII was the last active ruler of the Ptolemaic Kingdom.", Difficulty.MEDIUM,
                "Cleopatra VII", true, "Nefertiti", false, "Hatshepsut", false, "Sobekneferu", false);
        addTrueFalse(history, "The French Revolution began in 1789.", "The storming of the Bastille took place on July 14, 1789.", Difficulty.EASY, true);
        addMultiSelect(history, "Which countries formed the Triple Entente in World War I?", "Great Britain, France, and the Russian Empire formed the alliance.", Difficulty.MEDIUM,
                Arrays.asList("Great Britain", "France", "Russia"),
                Arrays.asList("Great Britain", "France", "Russia", "Germany", "Ottoman Empire"));
        addMcq(history, "What ancient civilization built Machu Picchu?", "Machu Picchu is an Incan citadel in the Andes mountains.", Difficulty.EASY,
                "Inca Empire", true, "Maya Civilization", false, "Aztec Empire", false, "Olmec Civilization", false);

        // 3. Geography (OpenTDB: 22)
        Topic geography = new Topic("Geography", "Physical geography, continents, nations, and oceans", 22);
        topicRepository.save(geography);

        addMcq(geography, "What is the longest river in the world?", "The Nile River in Africa is traditionally recognized as the longest river.", Difficulty.EASY,
                "Nile", true, "Amazon", false, "Yangtze", false, "Mississippi", false);
        addMcq(geography, "What is the highest mountain peak in the world?", "Mount Everest sits at 8,848.86 m in the Himalayas.", Difficulty.EASY,
                "Mount Everest", true, "K2", false, "Kangchenjunga", false, "Lhotse", false);
        addTrueFalse(geography, "Australia is both a continent and a country.", "Australia is the smallest continent and a single nation.", Difficulty.EASY, true);
        addTrueFalse(geography, "The Sahara is the largest desert in the world.", "Antarctica is the largest desert by area (cold desert).", Difficulty.MEDIUM, false);
        addMultiSelect(geography, "Which of the following countries are located in South America?", "Brazil, Argentina, and Chile are South American nations.", Difficulty.EASY,
                Arrays.asList("Brazil", "Argentina", "Chile"),
                Arrays.asList("Brazil", "Argentina", "Chile", "Spain", "Mexico"));
        addMcq(geography, "What is the capital city of Japan?", "Tokyo is Japan's capital and most populous metropolis.", Difficulty.EASY,
                "Tokyo", true, "Kyoto", false, "Osaka", false, "Hiroshima", false);
        addMcq(geography, "Which ocean is the deepest in the world?", "The Pacific Ocean contains the Mariana Trench.", Difficulty.EASY,
                "Pacific Ocean", true, "Atlantic Ocean", false, "Indian Ocean", false, "Arctic Ocean", false);
        addTrueFalse(geography, "Greenland is an independent country recognized by the UN.", "Greenland is an autonomous territory within the Kingdom of Denmark.", Difficulty.HARD, false);
        addMultiSelect(geography, "Which of the following rivers flow through Europe?", "Danube, Rhine, and Seine are European rivers.", Difficulty.MEDIUM,
                Arrays.asList("Danube", "Rhine", "Seine"),
                Arrays.asList("Danube", "Rhine", "Seine", "Ganges", "Zambezi"));
        addMcq(geography, "What is the smallest country in the world by land area?", "Vatican City covers only about 49 hectares (121 acres).", Difficulty.EASY,
                "Vatican City", true, "Monaco", false, "San Marino", false, "Liechtenstein", false);

        // 4. Mathematics (OpenTDB: 19)
        Topic math = new Topic("Mathematics", "Algebra, Calculus, Geometry, Probability, and Logic", 19);
        topicRepository.save(math);

        addMcq(math, "What is the value of Pi rounded to two decimal places?", "Pi is approximately 3.14159...", Difficulty.EASY,
                "3.14", true, "3.12", false, "3.16", false, "3.20", false);
        addMcq(math, "What is the square root of 144?", "12 x 12 = 144.", Difficulty.EASY,
                "12", true, "14", false, "10", false, "16", false);
        addTrueFalse(math, "Zero (0) is an even number.", "An integer is even if divisible by 2 with remainder 0; 0 / 2 = 0.", Difficulty.EASY, true);
        addTrueFalse(math, "Every prime number is an odd number.", "2 is the only even prime number.", Difficulty.EASY, false);
        addMultiSelect(math, "Which of the following are prime numbers?", "2, 3, 5, 7, 11, 13 are primes.", Difficulty.MEDIUM,
                Arrays.asList("2", "13", "19"),
                Arrays.asList("2", "13", "19", "9", "15"));
        addMcq(math, "What is the derivative of x^2 with respect to x?", "By power rule: d/dx(x^n) = n*x^(n-1).", Difficulty.MEDIUM,
                "2x", true, "x", false, "2", false, "x^3 / 3", false);
        addMcq(math, "In a right triangle, what relates the hypotenuse (c) to sides a and b?", "Pythagorean theorem: a^2 + b^2 = c^2.", Difficulty.EASY,
                "a^2 + b^2 = c^2", true, "a + b = c", false, "a^2 - b^2 = c^2", false, "ab = c^2", false);
        addTrueFalse(math, "The sum of interior angles in any Euclidean triangle is 180 degrees.", "Triangle interior angles always sum to 180° in Euclidean space.", Difficulty.EASY, true);
        addMultiSelect(math, "Which of the following functions are transcendental?", "Trigonometric and exponential functions are transcendental.", Difficulty.HARD,
                Arrays.asList("sin(x)", "e^x", "ln(x)"),
                Arrays.asList("sin(x)", "e^x", "ln(x)", "x^2 + 3", "2x - 5"));
        addMcq(math, "What is 7 factorial (7!)?", "7! = 7 * 6 * 5 * 4 * 3 * 2 * 1 = 5040.", Difficulty.HARD,
                "5040", true, "720", false, "40320", false, "2520", false);

        // 5. Technology (OpenTDB: 18)
        Topic technology = new Topic("Technology", "Computer Science, Software Engineering, AI, and Cloud", 18);
        topicRepository.save(technology);

        addMcq(technology, "What does HTTP stand for?", "HyperText Transfer Protocol is the foundation of data communication on the World Wide Web.", Difficulty.EASY,
                "HyperText Transfer Protocol", true, "High Text Transport Protocol", false, "Hyperlink Transfer Technology Program", false, "Home Tool Text Protocol", false);
        addMcq(technology, "Which company developed the Java programming language?", "Sun Microsystems created Java in 1995 (acquired by Oracle).", Difficulty.EASY,
                "Sun Microsystems", true, "Microsoft", false, "Apple", false, "IBM", false);
        addTrueFalse(technology, "Java source code compiles directly to machine code by default.", "Java compiles to bytecode (.class) which is executed by the JVM.", Difficulty.EASY, false);
        addTrueFalse(technology, "REST APIs commonly use JSON format for payload data exchange.", "JSON is the standard format for RESTful web services.", Difficulty.EASY, true);
        addMultiSelect(technology, "Which of the following are Object-Oriented Programming principles?", "Encapsulation, Inheritance, and Polymorphism are core OOP pillars.", Difficulty.MEDIUM,
                Arrays.asList("Encapsulation", "Inheritance", "Polymorphism"),
                Arrays.asList("Encapsulation", "Inheritance", "Polymorphism", "Compilation", "Paging"));
        addMcq(technology, "What is the default port for HTTPS traffic?", "Port 443 is the standard secured HTTP port.", Difficulty.EASY,
                "443", true, "80", false, "8080", false, "22", false);
        addMcq(technology, "What data structure operates on a First-In-First-Out (FIFO) basis?", "Queue follows FIFO order.", Difficulty.EASY,
                "Queue", true, "Stack", false, "Binary Tree", false, "Hash Map", false);
        addTrueFalse(technology, "In Spring Framework, @Component is a stereotype annotation.", "@Component is the root stereotype for Spring managed beans.", Difficulty.MEDIUM, true);
        addMultiSelect(technology, "Which of the following are NoSQL database systems?", "MongoDB, Cassandra, and Redis are non-relational NoSQL databases.", Difficulty.MEDIUM,
                Arrays.asList("MongoDB", "Cassandra", "Redis"),
                Arrays.asList("MongoDB", "Cassandra", "Redis", "PostgreSQL", "MySQL"));
        addMcq(technology, "What does ACID stand for in database management?", "Atomicity, Consistency, Isolation, Durability ensure database transaction reliability.", Difficulty.HARD,
                "Atomicity, Consistency, Isolation, Durability", true,
                "Access, Control, Integrity, Data", false,
                "Async, Cache, Index, Dispatch", false,
                "Authentication, Cipher, Identity, Defense", false);
    }

    private void seedQuizzes() {
        Topic tech = topicRepository.findByNameIgnoreCase("Technology").orElse(null);
        Topic science = topicRepository.findByNameIgnoreCase("Science").orElse(null);
        Topic geo = topicRepository.findByNameIgnoreCase("Geography").orElse(null);

        if (tech != null) {
            Quiz techQuiz = new Quiz("Full-Stack Software Engineering Assessment",
                    "Test your fundamental understanding of computer networks, databases, Java, and modern OOP paradigms.",
                    tech, 15, QuizStatus.ACTIVE, "TECH-101", "pass123");
            techQuiz.setQuestions(questionRepository.findByTopicId(tech.getId()));
            quizRepository.save(techQuiz);
        }

        if (science != null) {
            Quiz scienceQuiz = new Quiz("General Science & Natural Laws Master Quiz",
                    "Comprehensive exam covering elements, biology, mechanics, and astronomy.",
                    science, 20, QuizStatus.ACTIVE, "SCI-202", "science123");
            scienceQuiz.setQuestions(questionRepository.findByTopicId(science.getId()));
            quizRepository.save(scienceQuiz);
        }

        if (geo != null) {
            Quiz geoQuiz = new Quiz("World Geography & Capital Cities Challenge",
                    "Explore continents, mountain ranges, world capitals, and landmarks.",
                    geo, 10, QuizStatus.ACTIVE, "GEO-303", "geo123");
            geoQuiz.setQuestions(questionRepository.findByTopicId(geo.getId()));
            quizRepository.save(geoQuiz);
        }
    }

    private void addMcq(Topic topic, String text, String explanation, Difficulty diff,
                        String correctText, boolean isCorr,
                        String wrong1, boolean isCorr1,
                        String wrong2, boolean isCorr2,
                        String wrong3, boolean isCorr3) {
        McqQuestion q = new McqQuestion(text, explanation, diff, topic);
        q.addOption(new Option(q, correctText, isCorr));
        q.addOption(new Option(q, wrong1, isCorr1));
        q.addOption(new Option(q, wrong2, isCorr2));
        q.addOption(new Option(q, wrong3, isCorr3));
        questionRepository.save(q);
    }

    private void addTrueFalse(Topic topic, String text, String explanation, Difficulty diff, boolean isTrueCorrect) {
        TrueFalseQuestion q = new TrueFalseQuestion(text, explanation, diff, topic);
        q.addOption(new Option(q, "True", isTrueCorrect));
        q.addOption(new Option(q, "False", !isTrueCorrect));
        questionRepository.save(q);
    }

    private void addMultiSelect(Topic topic, String text, String explanation, Difficulty diff,
                                List<String> correctOptions, List<String> allOptions) {
        MultiSelectQuestion q = new MultiSelectQuestion(text, explanation, diff, topic);
        for (String optText : allOptions) {
            boolean isCorrect = correctOptions.contains(optText);
            q.addOption(new Option(q, optText, isCorrect));
        }
        questionRepository.save(q);
    }
}
