# User Stories

## Admin user stories (access + system functionality)

1. **Manage roles and permissions**  
   As an **admin**, I want to **create and manage roles (Admin/Doctor/Patient)** so that **users have appropriate access**.
   - **Acceptance criteria**
     - Admin can create/edit/delete roles.
     - Admin can assign permissions to roles.
     - Changes take effect immediately for newly authorized actions.

2. **Approve/verify doctor accounts**  
   As an **admin**, I want to **verify doctors before they can accept appointments** so that **patients only book with legitimate providers**.
   - **Acceptance criteria**
     - Doctor accounts have a “pending/verified/rejected” status.
     - Only verified doctors can publish availability or be bookable.

3. **Manage user accounts**  
   As an **admin**, I want to **activate, suspend, or reset user accounts** so that **I can respond to misuse and support requests**.
   - **Acceptance criteria**
     - Admin can suspend/reactivate accounts.
     - Admin can trigger password reset / account recovery flow.

4. **View system-wide appointment dashboard**  
   As an **admin**, I want a **system dashboard of all appointments** so that **I can monitor usage and troubleshoot scheduling issues**.
   - **Acceptance criteria**
     - Filter by date range, doctor, patient, status.
     - Export view (CSV) if enabled by the system.

5. **Configure clinic/system settings**  
   As an **admin**, I want to **configure scheduling rules** (slot length, working hours defaults, cancellation windows) so that **the system matches clinic policy**.
   - **Acceptance criteria**
     - Settings can be updated from an admin UI.
     - New bookings respect the updated rules.

6. **Audit and activity logs**  
   As an **admin**, I want to **review audit logs** so that **I can trace critical actions (role changes, cancellations, reschedules)**.
   - **Acceptance criteria**
     - Log entries include actor, action, timestamp, and affected resource.
     - Logs are searchable/filterable.


## Patient user stories (book + manage appointments)

1. **Find a doctor**  
   As a **patient**, I want to **search for doctors by specialty, location, or name** so that **I can choose the right provider**.
   - **Acceptance criteria**
     - Search results show doctor profile summary and next available slots.

2. **View doctor availability**  
   As a **patient**, I want to **see available appointment times** so that **I can pick a convenient slot**.
   - **Acceptance criteria**
     - Only open slots are selectable.
     - Time zone is clearly displayed.

3. **Book an appointment**  
   As a **patient**, I want to **book an appointment and receive confirmation** so that **I know my booking succeeded**.
   - **Acceptance criteria**
     - Booking requires selecting doctor + time + reason (optional/required per config).
     - Confirmation message and appointment appears in “My Appointments”.

4. **Reschedule an appointment**  
   As a **patient**, I want to **reschedule an existing appointment** so that **I can adjust when conflicts arise**.
   - **Acceptance criteria**
     - Reschedule respects cancellation/reschedule policy windows.
     - Old slot is released and new slot reserved atomically.

5. **Cancel an appointment**  
   As a **patient**, I want to **cancel an appointment** so that **I’m not marked as a no-show and the slot frees up**.
   - **Acceptance criteria**
     - Cancellation reason optional/required based on config.
     - Status updates to “cancelled” and doctor is notified (if notifications exist).

6. **View appointment history**  
   As a **patient**, I want to **see upcoming and past appointments** so that **I can keep track of my care**.
   - **Acceptance criteria**
     - Separate tabs/filters for Upcoming vs Past.
     - Each record shows date/time, doctor, status, and notes if permitted.


## Doctor user stories (availability + patient appointments)

1. **Set availability schedule**  
   As a **doctor**, I want to **define my weekly availability** so that **patients can book only when I’m working**.
   - **Acceptance criteria**
     - Doctor can set working days/hours.
     - Slot duration matches system rules or doctor-specific overrides (if allowed).

2. **Block time / time off**  
   As a **doctor**, I want to **block off time (vacation, meetings)** so that **patients cannot book during unavailable periods**.
   - **Acceptance criteria**
     - Blocking time prevents new bookings in that range.
     - Existing bookings in blocked time trigger a conflict workflow (policy-defined).

3. **View my appointment calendar**  
   As a **doctor**, I want to **see my upcoming appointments in a calendar/list** so that **I can plan my day**.
   - **Acceptance criteria**
     - View by day/week.
     - Appointment entries show patient name (as permitted), time, and reason.

4. **Approve or manage appointment requests (if applicable)**  
   As a **doctor**, I want to **accept/decline appointment requests** so that **I control my schedule when the system uses requests instead of instant booking**.
   - **Acceptance criteria**
     - Pending requests are visible.
     - Accepting confirms; declining releases the slot.

5. **Mark appointment status**  
   As a **doctor**, I want to **mark appointments as completed/no-show/cancelled** so that **records remain accurate**.
   - **Acceptance criteria**
     - Status changes are logged.
     - Patients can see appropriate status updates.

6. **Patient notes (optional depending on scope)**  
   As a **doctor**, I want to **add notes to an appointment** so that **I can document outcomes**.
   - **Acceptance criteria**
     - Notes are saved securely and only visible to authorized roles.
