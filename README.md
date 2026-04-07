# Spring Boot Security Demo Application

## Описание проекта
Spring Boot‑приложение с полной системой аутентификации и авторизации на основе ролей.  
Показывает типичную цепочку Spring Security, работу с MySQL через JPA, UI‑страницы (Thymeleaf) и набор REST‑эндпоинтов для управления пользователями и ролями.

> **Что изменилось в этой версии**
> - `UserDto` — поле `roleIds` удалено, пароль помечен  
>   `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)`.  
> - Удалены все `raw‑type`‑ы; теперь используется типизация (`List<UserDto>`, `Optional<Role>` и т.п.).  
> - Конфигурация безопасности полностью перенесена в `SecurityFilterChain`  
>   (устаревший `WebSecurityConfigurerAdapter` удалён).

## Технологический стек
- **Spring Boot 2.7.x** (Spring Security 5.7)  
- **Spring Security** – аутентификация, авторизация, CSRF‑исключения  
- **Spring Data JPA** – работа с MySQL  
- **MySQL 8.x** – основной источник данных (см. `application.yml`)  
- **Thymeleaf** – шаблонизатор UI  
- **Maven** – управление зависимостями  
- **Lombok** – уменьшает бойлерплейт  
- **MapStruct** – маппинг `User ↔ UserDto`

## Архитектура проекта
src/main/java/ru/kata/spring/boot_security/demo/ ├─ SpringBootSecurityDemoApplication.java // точка входа ├─ configs/ │ ├─ WebSecurityConfig.java // SecurityFilterChain │ ├─ SuccessUserHandler.java // кастомный success‑handler │ └─ DataInitializer.java // стартовые данные ├─ controller/ │ ├─ AdminRestController.java // REST‑API админа │ └─ UserController.java // UI‑контроллер (user‑info) ├─ service/ │ ├─ UserService.java │ ├─ RoleService.java │ └─ impl/ │ ├─ UserServiceImpl.java │ └─ RoleServiceImpl.java ├─ mapper/ │ └─ UserMapper.java // MapStruct ├─ dto/ │ └─ UserDto.java // DTO без roleIds, пароль Write‑only ├─ model/ │ ├─ User.java │ └─ Role.java ├─ repository/ │ ├─ UserRepository.java // JpaRepository<User, Long> │ └─ RoleRepository.java // JpaRepository<Role, Long> ├─ exception/ │ ├─ UserNotFoundException.java │ └─ RestExceptionHandler.java // @RestControllerAdvice └─ aspect/ └─ LoggingAspect.java


## Функциональные возможности
| Фича | Описание |
|------|----------|
| **Аутентификация** | Регистрация, вход по `email`/`password`, хранение пароля в виде BCrypt‑хеша. |
| **Авторизация** | Роли `ROLE_ADMIN`, `ROLE_USER`; защита эндпоинтов через `hasRole`/`hasAnyRole`. |
| **Управление пользователями** | Список, создание, редактирование, удаление (REST‑API). |
| **Управление ролями** | Просмотр всех ролей, назначение ролей пользователям. |
| **UI** | Главная, форма входа, регистрация, личный кабинет, админ‑панель. |
| **REST‑API** | `/api/admin/users`, `/api/admin/roles` (см. ниже). |

## Как запустить приложение

### Требования
- **JDK 17** или новее  
- **Maven 3.6+**  
- **MySQL** – создайте БД `crud_app_db` и пользователя `jpauser`/`jpauser`  
  (в `application.yml` уже прописан URL с `allowPublicKeyRetrieval=true`).

### Шаги
```bash
# 1. Клонировать репозиторий
git clone https://github.com/AntonStrokov/3.1.4-Rest-controllers.git
cd 3.1.4-Rest-controllers

# 2. Сборка проекта
mvn clean install

# 3. Запуск
mvn spring-boot:run
Приложение будет доступно по адресу http://localhost:8080.

Тестовые пользователи (создаются автоматически)
Пользователь	Email	Пароль	Роли
admin	admin@example.com	admin	ROLE_ADMIN, ROLE_USER
user	user@example.com	user	ROLE_USER
API Endpoints
Публичные
GET / – главная страница
GET /login – форма входа
POST /login – обработка входа
GET /registration – форма регистрации
POST /registration – обработка регистрации
Защищённые (требуется аутентификация)
Endpoint	Метод	Описание
/user	GET	Личный кабинет
/admin	GET	Админ‑панель
/admin/users	GET	Список всех пользователей (DTO)
/admin/users/{id}	GET	Информация о пользователе
/admin/users	POST	Создание нового пользователя
/admin/users/{id}	PUT	Обновление пользователя (можно менять пароль, роли)
/admin/users/{id}	DELETE	Удаление пользователя (идемпотентно)
/admin/roles	GET	Список всех ролей
Важно: поле password в UserDto не возвращается в ответах
(аннотация @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) и
UserMapper @Mapping(target = "password", ignore = true)).

Конфигурация безопасности (WebSecurityConfig.java)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final SuccessUserHandler successUserHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1️⃣ CSRF‑исключения только для API‑эндпоинтов
            .csrf(csrf -> csrf.ignoringAntMatchers("/api/**"))

            // 2️⃣ Авторизация запросов
            .authorizeRequests(auth -> auth
                    .antMatchers("/", "/login", "/error").permitAll()
                    .antMatchers("/api/**").permitAll()          // при необходимости ограничьте ролями
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                    .anyRequest().authenticated()
            )

            // 3️⃣ Форма входа
            .formLogin(form -> form
                    .loginPage("/login")
                    .successHandler(successUserHandler)
                    .permitAll()
            )

            // 4️⃣ Выход из системы
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}
Схема базы данных
-- Таблица пользователей
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    age INT
);

-- Таблица ролей
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Связь многие‑ко‑многим: пользователь ↔ роль
CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
Разработка
Добавление новой функциональности
Новая сущность – создайте модель в model/, репозиторий, сервис и контроллер.
Новый endpoint – добавьте метод в нужный контроллер и, при необходимости, обновите правила в WebSecurityConfig.
Новая роль – вставьте запись в таблицу roles и пропишите её в конфигурации доступа.
Тестирование
Рекомендуется добавить unit‑/integration‑тесты:

UserServiceImpl – маппинг, хеширование пароля, назначение ролей.
AdminRestController – валидация, статусы 200/400/404.
SecurityFilterChain – проверка доступа к открытым и закрытым эндпоинтам.
Частые проблемы и решения
Проблема	Как решить
Ошибка доступа к БД	Проверьте параметры в application.yml и наличие БД crud_app_db.
Ошибка аутентификации	Убедитесь, что пользователь существует и пароль (BCrypt) совпадает.
Ошибка авторизации	Проверьте, что у пользователя есть нужные роли и эндпоинт правильно сконфигурирован в WebSecurityConfig.
Лицензия
Проект распространяется под лицензией MIT.
