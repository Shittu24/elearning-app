# Muslim E-Learning Platform

A full-stack web application designed to provide young Muslims in the U.S. with structured, beginner-friendly Islamic education. The platform offers user registration, course enrollment, and interactive learning content, making religious knowledge more accessible and engaging.

## Features

- **User Authentication**
  - Register with email or Google sign-in (OAuth)
  - Secure session management

- **Courses & Lessons**
  - Browse and view all available Islamic courses (e.g., Aqidah, Quran, Stories of the Prophets)
  - Enroll in courses and track progress
  - View and complete lessons one by one
  - Automatic flow from lesson to lesson and course to course

- **User Dashboard**
  - Track enrolled courses
  - Resume incomplete lessons
  - View completed courses

## Tech Stack

- **Frontend:** Angular
- **Backend:** Java Spring Boot
- **Database:** PostgreSQL
- **Authentication:** Google OAuth, Spring Security

## How to Run Locally

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Shittu24/elearning-app.git
   ```

2. **Backend Setup (Spring Boot)**
   - Navigate to `elearning/`
   - Configure your database credentials in `application.properties`
   - Run the backend:
     ```bash
     ./mvnw spring-boot:run
     ```

3. **Frontend Setup (Angular)**
   - Navigate to `elearning-frontend/`
   - Install dependencies:
     ```bash
     npm install
     ```
   - Run Angular dev server:
     ```bash
     ng serve
     ```
   - Visit `http://localhost:4200` in your browser

## Future Improvements

- Add quiz functionality at the end of lessons
- Enable progress badges or certificates
- Admin dashboard to upload/manage courses and lessons
- Deploy to the cloud (e.g., AWS, Render, Netlify)

## Why This Project Matters

This project was built to serve a real need in the community: helping Muslim youth in the U.S. access authentic Islamic learning in a structured and modern way. It reflects my passion for building technology that makes an impact and demonstrates my ability to deliver full-stack solutions from concept to execution.
