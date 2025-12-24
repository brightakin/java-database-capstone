// doctorServices.js

import { API_BASE_URL } from "../config/config.js";

// Base Doctor API endpoint
const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Fetch all doctors
 * @returns {Array} List of doctor objects or empty array on error
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);
        if (!response.ok) throw new Error("Failed to fetch doctors.");
        const data = await response.json();
        return data || [];
    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

/**
 * Delete a doctor by ID (Admin only)
 * @param {string} id - Doctor's unique ID
 * @param {string} token - Admin auth token
 * @returns {Object} { success: boolean, message: string }
 */
export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            return { success: false, message: errorData.message || "Failed to delete doctor." };
        }

        const data = await response.json();
        return { success: true, message: data.message || "Doctor deleted successfully." };
    } catch (error) {
        console.error("Error deleting doctor:", error);
        return { success: false, message: "An unexpected error occurred." };
    }
}

/**
 * Add (save) a new doctor (Admin only)
 * @param {Object} doctor - Doctor details
 * @param {string} token - Admin auth token
 * @returns {Object} { success: boolean, message: string }
 */
export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        if (!response.ok) {
            const errorData = await response.json();
            return { success: false, message: errorData.message || "Failed to save doctor." };
        }

        const data = await response.json();
        return { success: true, message: data.message || "Doctor added successfully." };
    } catch (error) {
        console.error("Error saving doctor:", error);
        return { success: false, message: "An unexpected error occurred." };
    }
}

/**
 * Filter doctors based on name, time, and specialty
 * @param {string} name - Doctor name filter (optional)
 * @param {string} time - Time availability filter (optional)
 * @param {string} specialty - Specialty filter (optional)
 * @returns {Array} Filtered list of doctors
 */
export async function filterDoctors(name = "", time = "", specialty = "") {
    try {
        const params = new URLSearchParams();
        if (name) params.append("name", name);
        if (time) params.append("time", time);
        if (specialty) params.append("specialty", specialty);

        const url = `${DOCTOR_API}/filter?${params.toString()}`;
        const response = await fetch(url);

        if (!response.ok) throw new Error("Failed to filter doctors.");
        const data = await response.json();
        return data || [];
    } catch (error) {
        console.error("Error filtering doctors:", error);
        return [];
    }
}

/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/
