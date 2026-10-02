# ✈️ Triply — Travel Planner

**Triply** is a mobile Android application designed to help users plan, organize, and manage their trips in one place.

The application allows users to create trips, organize schedules, save interesting places, track expenses, add notes, and store travel photos.

The main goal of Triply is to make travel planning simple, convenient, and organized.

---

## 📱 About the Project

Planning a trip often requires using several different applications for schedules, expenses, notes, locations, and photos.

**Triply** combines these features into one mobile application.

Users can keep all important travel information together:

* 📅 Trip schedule
* 📍 Places to visit
* 💰 Expenses
* 📝 Notes
* 📸 Photos
* 🗓️ Daily plans

---

## 🎯 Project Goal

The main goal of the project is to develop a convenient Android application for organizing independent trips.

Triply helps users:

* Create and manage trips
* Plan activities for each day
* Save interesting places
* Track travel expenses
* Add personal notes
* Store travel photos
* Keep all trip-related information organized

---

## 👥 Target Audience

Triply is designed for:

* Independent travelers
* Students and young travelers
* People planning vacations
* Users who want to organize trips digitally
* Travelers who want to keep schedules, expenses, notes, and places in one application

---

## ✨ Main Features

### 🧳 Trip Management

Users can create and manage their trips.

Each trip can contain:

* Trip name
* Destination
* Start date
* End date
* Description

### 📅 Schedule

Users can create a schedule for their trip and organize activities by date and time.

The schedule helps users organize their plans for each day.

### 📍 Places

Users can save interesting places they want to visit.

Examples include:

* Restaurants
* Museums
* Parks
* Tourist attractions
* Hotels
* Other points of interest

### 💰 Expense Tracking

Triply allows users to keep track of travel expenses.

Users can record:

* Expense name
* Amount
* Category
* Date
* Description

### 📝 Notes

Users can create personal notes related to their trip.

Notes can be used for:

* Travel reminders
* Important information
* Personal ideas
* Useful addresses
* Recommendations

### 📸 Photos

Users can add photos to their trips and keep their travel memories together with other trip information.

---

## 🛠️ Technologies

| Technology             | Purpose                      |
| ---------------------- | ---------------------------- |
| **Kotlin**             | Main programming language    |
| **Jetpack Compose**    | User interface               |
| **Android Studio**     | Development environment      |
| **ViewModel**          | UI state and data management |
| **Navigation Compose** | Screen navigation            |
| **Room**               | Local database               |
| **DataStore**          | Local preferences            |
| **Hilt**               | Dependency injection         |
| **Git**                | Version control              |
| **GitHub**             | Source code management       |

---

## 🏗️ Architecture

Triply uses a structured Android architecture with separation between the user interface, application logic, and data layer.

```text
UI Layer
   ↓
ViewModel
   ↓
Repository
   ↓
Local Data Source
   ↓
Room Database
```

### UI Layer

Jetpack Compose is used to build the application's user interface.

### ViewModel

ViewModel manages UI-related data and application state.

### Repository

The Repository provides a clean connection between the ViewModel and data sources.

### Database

Room is used for local structured data storage.

---

## 🗄️ Main Data Entities

The application is based on several main entities:

```text
Trip
 ├── id
 ├── title
 ├── destination
 ├── startDate
 ├── endDate
 └── description

Place
 ├── id
 ├── tripId
 ├── name
 ├── location
 └── description

Schedule
 ├── id
 ├── tripId
 ├── date
 ├── time
 ├── title
 └── description

Expense
 ├── id
 ├── tripId
 ├── title
 ├── amount
 ├── category
 └── date

Note
 ├── id
 ├── tripId
 ├── title
 ├── content
 └── date

Photo
 ├── id
 ├── tripId
 └── photoUri
```

The `tripId` field connects related information to a specific trip.

---

## 📱 Main Screens

The application includes the following planned screens:

1. **Home Screen**
2. **Trip List**
3. **Create Trip**
4. **Trip Details**
5. **Schedule**
6. **Places**
7. **Expenses**
8. **Notes**
9. **Photos**
10. **Settings**

### Navigation

```text
Home
 │
 ├── Trips
 │    ├── Create Trip
 │    └── Trip Details
 │          ├── Schedule
 │          ├── Places
 │          ├── Expenses
 │          ├── Notes
 │          └── Photos
 │
 └── Settings
```

---

## ⚙️ Installation

### Requirements

Before running the project, make sure you have:

* Android Studio
* JDK
* Android SDK
* Git
* Android Emulator or a physical Android device

### Clone the Repository

```bash
git clone https://github.com/dildebekova/triply.git
```

Navigate to the project directory:

```bash
cd triply
```

Open the project in **Android Studio**.

Allow Gradle to synchronize the project and download the required dependencies.

---

## 🚀 How to Run

1. Clone the repository.
2. Open the project in Android Studio.
3. Wait for Gradle synchronization.
4. Connect an Android device or start an emulator.
5. Select the `app` configuration.
6. Click **Run ▶**.

---

## 📂 Project Structure

```text
Triply/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── ...
│           │
│           ├── res/
│           │   └── ...
│           │
│           └── AndroidManifest.xml
│
├── gradle/
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

The project structure may change during development.

---

## 🔐 Data Management

Triply is designed to store the user's trip information locally on the device.

Room Database is used for structured application data such as:

* Trips
* Places
* Schedules
* Expenses
* Notes

DataStore is used for lightweight application preferences and settings.

---

## 🔮 Future Improvements

Possible future improvements include:

* 🌍 Interactive maps
* 📍 GPS-based location features
* ☁️ Cloud synchronization
* 👥 Shared trips
* 🔔 Travel reminders
* 🌦️ Weather information
* 💱 Currency conversion
* 🔐 User authentication
* ☁️ Cloud photo storage
* 📊 Expense statistics
* 🌐 Multi-language support

---

## 🎓 Educational Purpose

Triply is an independent university project developed to practice modern Android development and software engineering concepts.

The project provides practical experience with:

* Kotlin
* Android development
* Jetpack Compose
* MVVM architecture
* Local databases
* Navigation
* Dependency injection
* Git and GitHub
* Application design
* Software project planning

---

## 👩‍💻 Author

**Nazima Dildebekova**

Software Engineering Student

GitHub:
https://github.com/dildebekova

Project Repository:
https://github.com/dildebekova/triply

---

## 📌 Project Status

🚧 **In Development**

Triply is currently under active development as a university independent project.

Features and application architecture may change during development.
