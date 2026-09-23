# QuizHub - Online Quiz Management System

QuizHub is an educational SaaS and online quiz management platform built with **Spring Boot 3**, **Thymeleaf**, and **Tailwind CSS**. The user interface is crafted to match the QuizHub Dashboard design system with dual light & dark themes, indigo/teal/amber accents, and 12px rounded cards.

---

## Key Features

1. **Polymorphic Question Engine (OOP-driven)**:
   - Question entities inherit from an abstract base class `Question` with subclasses `McqQuestion`, `TrueFalseQuestion`, and `MultiSelectQuestion`.
   - Each subclass polymorphically implements `public abstract boolean checkAnswer(List<Long> selectedOptionIds)`.
   - On quiz submission, scoring executes directly through polymorphic dispatch (`q.checkAnswer(...)`) with **zero** `if/else` or `switch` branching on question types.

2. **Question Bank (CRUD & Filtering)**:
   - Create, edit, delete, and paginate through questions.
   - Filter by Topic, Difficulty (`EASY`, `MEDIUM`, `HARD`), and real-time keyword search (`findByTextContainingIgnoreCase`).

3. **External Question Import (Open Trivia DB API)**:
   - Real-time question import from [Open Trivia Database API](https://opentdb.com/api.php).
   - Topic category mappings:
     - **Science**: Category ID `17`
     - **History**: Category ID `23`
     - **Geography**: Category ID `22`
     - **Mathematics**: Category ID `19`
     - **Technology**: Category ID `18`
   - Automatically unescapes HTML entities (`&quot;`, `&#039;`, `&amp;`) and wraps any network/service failures into a custom `QuestionFetchException`.

4. **Topic Domain Management**:
   - Card grid overview showing question counts by difficulty tier (Easy, Medium, Hard), total questions, active quizzes, and syllabus completion meters.

5. **Quiz Builder & Selection Modes**:
   - Create quizzes by selecting topic, duration, and passing threshold.
   - **Auto Selection**: Random auto-selection using `Collections.shuffle` on the filtered difficulty pool.
   - **Manual Selection**: Select specific questions with an interactive selector.

6. **Quiz Conducting & Live Autosave**:
   - Interactive single-question navigator with visual progress bar and question status palette (Answered, Unanswered, Current).
   - **Server-side timer validation**: Timer countdown is enforced on the backend against `attempt.getStartedAt() + quiz.getDurationMinutes()` with a 30s grace buffer before rejecting late submissions with `QuizTimeExpiredException`.
   - Instant AJAX autosave as the student answers questions (`/api/attempt/save`).

7. **Results & Answer Review**:
   - Score summary banner, Pass/Fail status chip, and per-topic mastery breakdown.
   - Detailed review screen contrasting student choices against correct answers, complete with explanations.

8. **Global Search Bar**:
   - Top-bar search (with `⌘K` / `Ctrl+K` keyboard shortcut) querying across topics, questions, and quizzes.

9. **Security & Role-Based Access Control**:
   - Spring Security form login with role-based routing (`ROLE_ADMIN` and `ROLE_STUDENT`).
   - Admin-only routes for Question Bank, Topics management, Quiz creation, and OpenTDB import.

10. **Centralized Exception Handling (`@ControllerAdvice`)**:
    - Central handling for `QuizNotFoundException`, `QuizTimeExpiredException`, `InvalidAnswerException`, and `QuestionFetchException` rendering user-friendly error views.

---

## Tech Stack

- **Backend**: Java 21+, Spring Boot 3.3.4 (Spring Data JPA, Spring Security, Spring Web, Validation)
- **Frontend**: Thymeleaf 3, Tailwind CSS (via Tailwind CDN + Custom Config), Google Inter Font, Material Symbols Outlined
- **Database**:
  - **H2 in-memory Database** (Default active profile for zero-config startup)
  - **MySQL 8.x** (Configured via `mysql` Spring profile)
- **Build Tool**: Maven 3.9.x (Maven Wrapper `mvnw` / `mvnw.cmd` included)
- **Testing**: JUnit 5 + Mockito

---

## Pre-seeded Default Accounts

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@quizhub.com` | `admin123` | Full access to Dashboard, Question Bank, Topics CRUD, Create Quiz, Import, and Results |
| **Student** | `student@quizhub.com` | `student123` | Access to Dashboard, Topics, Live Quizzes, Quiz Taking, and Results |

*Note: The login page includes quick-fill buttons for both demo accounts.*

---

## Running the Application Locally

### 1. Run with Default H2 Database (Recommended for quick testing)

```bash
# On Windows (PowerShell or CMD)
.\mvnw.cmd spring-boot:run

# On Linux / macOS
./mvnw spring-boot:run
```

The application will start on **`http://localhost:8080`**.

Pre-seeded database automatically populates:
- 2 Users (`admin@quizhub.com` & `student@quizhub.com`)
- 5 Topics (Science, History, Geography, Mathematics, Technology)
- 50+ polymorphic questions (MCQ, True/False, Multi-Select)
- 3 Active Assessment Quizzes

H2 Web Console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:quizhubdb`, Username: `sa`, Password: empty).

---

### 2. Run with MySQL Database

1. Ensure MySQL is running on port `3306`.
2. Update connection credentials in `src/main/resources/application-mysql.yml` if needed:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/quizhub?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
       username: root
       password: your_password
   ```
3. Run with the `mysql` active profile:
   ```bash
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## Running Unit Tests

Run all unit tests covering polymorphic scoring logic and exception handling:

```bash
.\mvnw.cmd test
```

### Test Suites Included:
- **`PolymorphicScoringTest`**: Tests polymorphic `checkAnswer()` for MCQ, True/False, MultiSelect, and heterogeneous question lists.
- **`ExceptionHandlingTest`**: Tests `@ControllerAdvice` global exception handling paths and custom error views.
- **`AttemptServiceTest`**: Mockito tests verifying attempt initialization, scoring computation, and server-side timer validation.
