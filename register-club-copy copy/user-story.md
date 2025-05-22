**Title**: Student Club Registration with Club Limit

**As a** Student
**I want** to register for school clubs and select up to two clubs
**So that** I can participate in extracurricular activities and explore my interests.

**Business Logic**:
- Students can register for a maximum of 2 clubs.
- Club registration is only open during the designated registration period.

**Acceptance Criteria**:
1. A student can successfully register for an account with a password 
2. A student can browse a list of available clubs.
3. A student can select up to two clubs during registration.
4. The system prevents a student from registering for more than two clubs.
5. The system displays a confirmation message after successful club registration.

**Functional Requirements**:
- User registration: Allow students to create an account with a unique username, password, and other necessary information (e.g., student ID, name, grade).
- Club listing: Display a list of available clubs with descriptions.
- Club selection: Allow students to select clubs from the list.
- Club limit enforcement: Enforce the rule that a student can only register for a maximum of two clubs.
- Registration confirmation: Display a confirmation message after successful registration, listing the selected clubs.
- Error handling: Display appropriate error messages if the student tries to register for more than two clubs or if there are any other registration issues.

**Non-Functional Requirements**:
- Performance: The registration process should be quick and efficient.
- Security: Student data should be stored securely.
- Usability: The registration process should be easy to understand and use.
- Accessibility: The system should be accessible to students with disabilities.

**UI Design**:
- Registration form: A clear and concise registration form with fields for username, password, student ID, name, grade, etc.
- Club list: A visually appealing list of clubs with descriptions and images (optional).
- Selection mechanism: Checkboxes or similar UI elements to allow students to select clubs.
- Confirmation message: A clear and prominent confirmation message after successful registration.
- Error messages: User-friendly error messages to guide students through the registration process.
