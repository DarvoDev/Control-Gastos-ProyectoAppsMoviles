<div align="center">

# 💼 Control de Gastos

**App Android para registrar tus gastos, cuidar tu presupuesto mensual y recibir recordatorios.**

![Android](https://img.shields.io/badge/Android-24%2B-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-local-003B57?logo=sqlite&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-DayNight-1565C0)

</div>

---

## ✨ Funciones

| | Función | Cómo funciona |
|---|---|---|
| 🔐 | **Registro e inicio de sesión** | Usuarios en SQLite, contraseña guardada como hash SHA-256 (nunca en texto plano) |
| 🔁 | **Sesión persistente** | Al volver a abrir la app entras directo, sin iniciar sesión otra vez |
| 🧾 | **Registro de gastos** | Monto, descripción, categoría y fecha |
| 📋 | **Lista con filtros** | Por mes y por categoría, con total filtrado |
| 📊 | **Dashboard del mes** | Total gastado, barra de presupuesto y gastos recientes |
| 🌙 | **Modo oscuro** | Se guarda y se aplica desde que arranca la app |
| 💾 | **Elegir almacenamiento** | SQLite o archivo JSON interno, desde Ajustes |
| 🔔 | **Notificaciones locales** | Alerta al acercarte al límite del presupuesto y recordatorio diario |

---

## 🧱 Tecnologías

- **Lenguaje:** Java 11
- **UI:** Activities + layouts XML con widgets nativos (`LinearLayout`, `FrameLayout`, `ScrollView`, `ListView`)
- **Tema:** `Theme.Material3.DayNight.NoActionBar` con colores en `values/` y `values-night/`
- **Persistencia:** SQLite (`SQLiteOpenHelper`), archivo JSON en almacenamiento interno, `SharedPreferences`
- **Notificaciones:** `NotificationChannel`, `AlarmManager`, `BroadcastReceiver`
- **Sin librerías externas**
- `minSdk 24` · `targetSdk / compileSdk 36`

---

## 🗂️ Estructura

```
app/src/main/java/com/example/controlgastos/
├── App.java                  Application: aplica el modo oscuro al arrancar
├── activities/               Splash, Login, Register, Main, GastoForm, GastosList, Settings
├── adapters/                 GastoAdapter (ListView)
├── database/                 DBHelper, UserDao, GastoDao
├── models/                   User, Gasto
├── repository/               GastoRepository (interfaz)
│                             ├── SqliteGastoRepository
│                             ├── ArchivoGastoRepository (JSON)
│                             └── RepositoryFactory
├── receivers/                ReminderReceiver, BootReceiver
└── utils/                    PrefsManager, HashUtil, NotificationHelper, ReminderScheduler
```

### Flujo de pantallas

```
Splash ──(¿hay sesión?)──► Main ──► Nuevo gasto
   │                          ├──► Lista de gastos
   │                          └──► Ajustes ──► Cerrar sesión ──► Login
   └──(no)──► Login ◄──► Registro
```

### ¿Dónde se guarda cada cosa?

| Dato | Dónde |
|---|---|
| Usuarios | SQLite → tabla `users` |
| Gastos | SQLite → tabla `gastos` **o** `files/gastos.json` (según Ajustes) |
| Sesión, modo oscuro, presupuesto, almacenamiento, notificaciones | `SharedPreferences` → `control_gastos_prefs` |

<details>
<summary><b>Esquema de la base de datos</b></summary>

```sql
CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL
);

CREATE TABLE gastos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER NOT NULL,
  categoria TEXT NOT NULL,
  monto REAL NOT NULL,
  descripcion TEXT,
  fecha TEXT NOT NULL,              -- yyyy-MM-dd
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

</details>

<details>
<summary><b>Patrón repositorio</b></summary>

Las pantallas nunca saben dónde se guardan los gastos: piden el repositorio a la fábrica, y la fábrica decide según lo elegido en Ajustes.

```java
GastoRepository repo = RepositoryFactory.get(this);   // SQLite o archivo JSON
repo.insertar(gasto);
List<Gasto> lista = repo.listar(userId, desde, hasta, categoria);
double total = repo.totalPeriodo(userId, desde, hasta);
```

> SQLite y el archivo son almacenamientos separados: lo que guardes en uno no aparece en el otro.

</details>

---

## 🚀 Cómo correrlo

1. Clona el repositorio:
   ```bash
   git clone https://github.com/DarvoDev/Control-Gastos-ProyectoAppsMoviles.git
   ```
2. Ábrelo en **Android Studio** (*File → Open*) y espera a que sincronice Gradle.
3. Elige un emulador o un teléfono con **Android 7.0 (API 24) o superior**.
4. Presiona ▶️ **Run**.

Desde la terminal:
```bash
./gradlew assembleDebug        # genera app/build/outputs/apk/debug/app-debug.apk
```
> Gradle necesita JDK 17 o superior. Si tu terminal usa uno más viejo, apunta `JAVA_HOME` al JDK de Android Studio (carpeta `jbr`).

---

## 👥 Equipo

Proyecto para la materia **Desarrollo de Aplicaciones Móviles**, dividido en 4 módulos:

| Módulo | Incluye |
|---|---|
| **Usuarios y sesión** | Base de datos, registro, login, hash de contraseñas, `PrefsManager` |
| **Gastos** | Modelo, repositorio SQLite, formulario, lista y adapter |
| **Ajustes y almacenamiento** | Modo oscuro, presupuesto, elegir SQLite / archivo JSON, icono |
| **Dashboard y notificaciones** | Pantalla principal, canal, alertas de presupuesto, recordatorio diario |

### Cómo colaborar

1. Trabaja en tu rama y antes de empezar: `git pull origin main`.
2. Commits pequeños y con mensaje claro.
3. Abre un Pull Request a `main`; otro integrante lo revisa.
4. Obtén los gastos **siempre** con `RepositoryFactory.get(this)`, nunca con `new SqliteGastoRepository(...)`.
