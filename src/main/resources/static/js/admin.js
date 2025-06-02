/**
 * Admin panel functionality
 */

// Bootstrap modal instances
let courseModal, lessonModal, exerciseModal;

// Current selected course and lesson IDs
let currentCourseId = null;
let currentLessonId = null;

// Initialize the admin panel
document.addEventListener('DOMContentLoaded', () => {
    // Initialize Bootstrap modals
    courseModal = new bootstrap.Modal(document.getElementById('courseModal'));
    lessonModal = new bootstrap.Modal(document.getElementById('lessonModal'));
    exerciseModal = new bootstrap.Modal(document.getElementById('exerciseModal'));

    // Setup navigation
    setupNavigation();
    
    // Setup event listeners
    setupEventListeners();
    
    // Load initial data
    loadDashboardData();
    loadCourses();
});

// Setup navigation between sections
function setupNavigation() {
    const sections = ['dashboard', 'courses', 'lessons', 'exercises', 'users'];
    
    sections.forEach(section => {
        const link = document.getElementById(`${section}-link`);
        if (link) {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                
                // Hide all sections
                sections.forEach(s => {
                    const sectionElement = document.getElementById(`${s}-section`);
                    if (sectionElement) {
                        sectionElement.classList.add('d-none');
                    }
                    
                    const sectionLink = document.getElementById(`${s}-link`);
                    if (sectionLink) {
                        sectionLink.classList.remove('active');
                    }
                });
                
                // Show selected section
                const sectionElement = document.getElementById(`${section}-section`);
                if (sectionElement) {
                    sectionElement.classList.remove('d-none');
                }
                
                link.classList.add('active');
                
                // Load section-specific data
                if (section === 'dashboard') {
                    loadDashboardData();
                } else if (section === 'courses') {
                    loadCourses();
                }
            });
        }
    });
}

// Setup event listeners for buttons and forms
function setupEventListeners() {
    // Course events
    document.getElementById('add-course-btn').addEventListener('click', () => {
        document.getElementById('course-form').reset();
        document.getElementById('course-id').value = '';
        document.getElementById('courseModalLabel').textContent = 'Add Course';
        courseModal.show();
    });
    
    document.getElementById('save-course-btn').addEventListener('click', saveCourse);
    
    // Lesson events
    document.getElementById('course-select').addEventListener('change', (e) => {
        const courseId = e.target.value;
        if (courseId && courseId !== 'Select Course') {
            currentCourseId = courseId;
            loadLessons(courseId);
            document.getElementById('add-lesson-btn').disabled = false;
        } else {
            document.getElementById('add-lesson-btn').disabled = true;
            document.getElementById('lessons-table-body').innerHTML = '';
        }
    });
    
    document.getElementById('add-lesson-btn').addEventListener('click', () => {
        document.getElementById('lesson-form').reset();
        document.getElementById('lesson-id').value = '';
        document.getElementById('lessonModalLabel').textContent = 'Add Lesson';
        lessonModal.show();
    });
    
    document.getElementById('save-lesson-btn').addEventListener('click', saveLesson);
    
    // Exercise events
    document.getElementById('lesson-select').addEventListener('change', (e) => {
        const lessonId = e.target.value;
        if (lessonId && lessonId !== 'Select Lesson') {
            currentLessonId = lessonId;
            loadExercises(lessonId);
            document.getElementById('add-exercise-btn').disabled = false;
        } else {
            document.getElementById('add-exercise-btn').disabled = true;
            document.getElementById('exercises-table-body').innerHTML = '';
        }
    });
    
    document.getElementById('add-exercise-btn').addEventListener('click', () => {
        document.getElementById('exercise-form').reset();
        document.getElementById('exercise-id').value = '';
        document.getElementById('exerciseModalLabel').textContent = 'Add Exercise';
        
        // Hide all exercise type fields
        document.querySelectorAll('.exercise-type-fields').forEach(field => {
            field.classList.add('d-none');
        });
        
        exerciseModal.show();
    });
    
    document.getElementById('exercise-type').addEventListener('change', (e) => {
        const type = e.target.value;
        
        // Hide all exercise type fields
        document.querySelectorAll('.exercise-type-fields').forEach(field => {
            field.classList.add('d-none');
        });
        
        // Show fields for selected type
        if (type) {
            const typeFields = document.getElementById(`${type.toLowerCase()}-fields`);
            if (typeFields) {
                typeFields.classList.remove('d-none');
            }
        }
    });
    
    document.getElementById('save-exercise-btn').addEventListener('click', saveExercise);
    
    // Multiple choice options
    document.getElementById('add-option-btn').addEventListener('click', addMultipleChoiceOption);
    
    // Matching pairs
    document.getElementById('add-pair-btn').addEventListener('click', addMatchingPair);
    
    // User promotion
    document.getElementById('promote-btn').addEventListener('click', promoteToAdmin);
}

// Load dashboard data
async function loadDashboardData() {
    try {
        // Load course count
        const coursesResponse = await fetch('/api/admin/courses', {
            method: 'GET',
            headers: getAuthHeaders()
        });
        
        if (coursesResponse.ok) {
            const courses = await coursesResponse.json();
            document.getElementById('course-count').textContent = courses.length;
        }
        
        // We would need additional endpoints to get lesson and exercise counts
        // For now, just display placeholders
        document.getElementById('lesson-count').textContent = 'N/A';
        document.getElementById('exercise-count').textContent = 'N/A';
        
    } catch (error) {
        console.error('Error loading dashboard data:', error);
        showError('Failed to load dashboard data');
    }
}

// Load courses
async function loadCourses() {
    try {
        const response = await fetch('/api/admin/courses', {
            method: 'GET',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to load courses');
        }
        
        const courses = await response.json();
        
        // Populate courses table
        const tableBody = document.getElementById('courses-table-body');
        tableBody.innerHTML = '';
        
        courses.forEach(course => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${course.name}</td>
                <td>${course.language}</td>
                <td>${course.description || ''}</td>
                <td class="action-buttons">
                    <button class="btn btn-sm btn-primary edit-course" data-id="${course.id}">Edit</button>
                    <button class="btn btn-sm btn-danger delete-course" data-id="${course.id}">Delete</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
        
        // Add event listeners to edit and delete buttons
        document.querySelectorAll('.edit-course').forEach(button => {
            button.addEventListener('click', (e) => {
                const courseId = e.target.getAttribute('data-id');
                editCourse(courseId);
            });
        });
        
        document.querySelectorAll('.delete-course').forEach(button => {
            button.addEventListener('click', (e) => {
                const courseId = e.target.getAttribute('data-id');
                deleteCourse(courseId);
            });
        });
        
        // Populate course select dropdowns
        populateCourseSelects(courses);
        
    } catch (error) {
        console.error('Error loading courses:', error);
        showError('Failed to load courses');
    }
}

// Populate course select dropdowns
function populateCourseSelects(courses) {
    const courseSelect = document.getElementById('course-select');
    courseSelect.innerHTML = '<option selected>Select Course</option>';
    
    courses.forEach(course => {
        const option = document.createElement('option');
        option.value = course.id;
        option.textContent = course.name;
        courseSelect.appendChild(option);
    });
}

// Load lessons for a course
async function loadLessons(courseId) {
    try {
        const response = await fetch(`/api/admin/lessons/course/${courseId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to load lessons');
        }
        
        const lessons = await response.json();
        
        // Populate lessons table
        const tableBody = document.getElementById('lessons-table-body');
        tableBody.innerHTML = '';
        
        lessons.forEach(lesson => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${lesson.title}</td>
                <td>${lesson.order}</td>
                <td>${lesson.xpReward}</td>
                <td class="action-buttons">
                    <button class="btn btn-sm btn-primary edit-lesson" data-id="${lesson.id}">Edit</button>
                    <button class="btn btn-sm btn-danger delete-lesson" data-id="${lesson.id}">Delete</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
        
        // Add event listeners to edit and delete buttons
        document.querySelectorAll('.edit-lesson').forEach(button => {
            button.addEventListener('click', (e) => {
                const lessonId = e.target.getAttribute('data-id');
                editLesson(lessonId);
            });
        });
        
        document.querySelectorAll('.delete-lesson').forEach(button => {
            button.addEventListener('click', (e) => {
                const lessonId = e.target.getAttribute('data-id');
                deleteLesson(lessonId);
            });
        });
        
        // Populate lesson select dropdown
        populateLessonSelect(lessons);
        
    } catch (error) {
        console.error('Error loading lessons:', error);
        showError('Failed to load lessons');
    }
}

// Populate lesson select dropdown
function populateLessonSelect(lessons) {
    const lessonSelect = document.getElementById('lesson-select');
    lessonSelect.innerHTML = '<option selected>Select Lesson</option>';
    
    lessons.forEach(lesson => {
        const option = document.createElement('option');
        option.value = lesson.id;
        option.textContent = lesson.title;
        lessonSelect.appendChild(option);
    });
}

// Load exercises for a lesson
async function loadExercises(lessonId) {
    try {
        const response = await fetch(`/api/admin/exercises/lesson/${lessonId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to load exercises');
        }
        
        const exercises = await response.json();
        
        // Populate exercises table
        const tableBody = document.getElementById('exercises-table-body');
        tableBody.innerHTML = '';
        
        exercises.forEach(exercise => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${exercise.type || getExerciseType(exercise)}</td>
                <td>${exercise.question}</td>
                <td>${exercise.xpReward}</td>
                <td class="action-buttons">
                    <button class="btn btn-sm btn-primary edit-exercise" data-id="${exercise.id}">Edit</button>
                    <button class="btn btn-sm btn-danger delete-exercise" data-id="${exercise.id}">Delete</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
        
        // Add event listeners to edit and delete buttons
        document.querySelectorAll('.edit-exercise').forEach(button => {
            button.addEventListener('click', (e) => {
                const exerciseId = e.target.getAttribute('data-id');
                editExercise(exerciseId);
            });
        });
        
        document.querySelectorAll('.delete-exercise').forEach(button => {
            button.addEventListener('click', (e) => {
                const exerciseId = e.target.getAttribute('data-id');
                deleteExercise(exerciseId);
            });
        });
        
    } catch (error) {
        console.error('Error loading exercises:', error);
        showError('Failed to load exercises');
    }
}

// Determine exercise type from object structure
function getExerciseType(exercise) {
    if (exercise.correctAnswer) {
        return 'TRANSLATION';
    } else if (exercise.options) {
        return 'MULTIPLE_CHOICE';
    } else if (exercise.pairs) {
        return 'MATCHING';
    }
    return 'Unknown';
}

// Save course
async function saveCourse() {
    try {
        const courseId = document.getElementById('course-id').value;
        const course = {
            name: document.getElementById('course-name').value,
            language: document.getElementById('course-language').value,
            description: document.getElementById('course-description').value,
            iconUrl: document.getElementById('course-icon-url').value
        };
        
        let url = '/api/admin/courses';
        let method = 'POST';
        
        if (courseId) {
            url = `/api/admin/courses/${courseId}`;
            method = 'PUT';
            course.id = courseId;
        }
        
        const response = await fetch(url, {
            method: method,
            headers: getAuthHeaders(),
            body: JSON.stringify(course)
        });
        
        if (!response.ok) {
            throw new Error('Failed to save course');
        }
        
        courseModal.hide();
        loadCourses();
        showSuccess('Course saved successfully');
        
    } catch (error) {
        console.error('Error saving course:', error);
        showError('Failed to save course');
    }
}

// Edit course
async function editCourse(courseId) {
    try {
        const response = await fetch(`/api/admin/courses/${courseId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to load course');
        }
        
        const course = await response.json();
        
        document.getElementById('course-id').value = course.id;
        document.getElementById('course-name').value = course.name;
        document.getElementById('course-language').value = course.language;
        document.getElementById('course-description').value = course.description || '';
        document.getElementById('course-icon-url').value = course.iconUrl || '';
        
        document.getElementById('courseModalLabel').textContent = 'Edit Course';
        courseModal.show();
        
    } catch (error) {
        console.error('Error loading course:', error);
        showError('Failed to load course');
    }
}

// Delete course
async function deleteCourse(courseId) {
    if (!confirm('Are you sure you want to delete this course? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/admin/courses/${courseId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to delete course');
        }
        
        loadCourses();
        showSuccess('Course deleted successfully');
        
    } catch (error) {
        console.error('Error deleting course:', error);
        showError('Failed to delete course');
    }
}

// Save lesson
async function saveLesson() {
    try {
        const lessonId = document.getElementById('lesson-id').value;
        const lesson = {
            title: document.getElementById('lesson-title').value,
            order: parseInt(document.getElementById('lesson-order').value),
            xpReward: parseInt(document.getElementById('lesson-xp-reward').value),
            iconUrl: document.getElementById('lesson-icon-url').value
        };
        
        let url = `/api/admin/lessons/course/${currentCourseId}`;
        let method = 'POST';
        
        if (lessonId) {
            url = `/api/admin/lessons/${lessonId}`;
            method = 'PUT';
            lesson.id = lessonId;
        }
        
        const response = await fetch(url, {
            method: method,
            headers: getAuthHeaders(),
            body: JSON.stringify(lesson)
        });
        
        if (!response.ok) {
            throw new Error('Failed to save lesson');
        }
        
        lessonModal.hide();
        loadLessons(currentCourseId);
        showSuccess('Lesson saved successfully');
        
    } catch (error) {
        console.error('Error saving lesson:', error);
        showError('Failed to save lesson');
    }
}

// Edit lesson
async function editLesson(lessonId) {
    try {
        // We need to get the lesson details
        // This endpoint might need to be created
        const response = await fetch(`/api/admin/lessons/${lessonId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to load lesson');
        }
        
        const lesson = await response.json();
        
        document.getElementById('lesson-id').value = lesson.id;
        document.getElementById('lesson-title').value = lesson.title;
        document.getElementById('lesson-order').value = lesson.order;
        document.getElementById('lesson-xp-reward').value = lesson.xpReward;
        document.getElementById('lesson-icon-url').value = lesson.iconUrl || '';
        
        document.getElementById('lessonModalLabel').textContent = 'Edit Lesson';
        lessonModal.show();
        
    } catch (error) {
        console.error('Error loading lesson:', error);
        showError('Failed to load lesson');
    }
}

// Delete lesson
async function deleteLesson(lessonId) {
    if (!confirm('Are you sure you want to delete this lesson? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/admin/lessons/${lessonId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to delete lesson');
        }
        
        loadLessons(currentCourseId);
        showSuccess('Lesson deleted successfully');
        
    } catch (error) {
        console.error('Error deleting lesson:', error);
        showError('Failed to delete lesson');
    }
}

// Save exercise
async function saveExercise() {
    try {
        const exerciseId = document.getElementById('exercise-id').value;
        const exerciseType = document.getElementById('exercise-type').value;
        
        if (!exerciseType) {
            showError('Please select an exercise type');
            return;
        }
        
        const exercise = {
            question: document.getElementById('exercise-question').value,
            hint: document.getElementById('exercise-hint').value,
            exerciseOrder: parseInt(document.getElementById('exercise-order').value),
            xpReward: parseInt(document.getElementById('exercise-xp-reward').value),
            heartsCost: parseInt(document.getElementById('exercise-hearts-cost').value)
        };
        
        // Add type-specific fields
        if (exerciseType === 'TRANSLATION') {
            exercise.correctAnswer = document.getElementById('translation-correct-answer').value;
        } else if (exerciseType === 'MULTIPLE_CHOICE') {
            exercise.options = [];
            document.querySelectorAll('.option-input').forEach(input => {
                exercise.options.push(input.value);
            });
            
            const selectedOption = document.querySelector('input[name="correctOption"]:checked');
            exercise.correctOptionIndex = parseInt(selectedOption.value);
        } else if (exerciseType === 'MATCHING') {
            exercise.pairs = {};
            const keys = document.querySelectorAll('.pair-key');
            const values = document.querySelectorAll('.pair-value');
            
            for (let i = 0; i < keys.length; i++) {
                exercise.pairs[keys[i].value] = values[i].value;
            }
        }
        
        let url = `/api/admin/exercises/lesson/${currentLessonId}`;
        let method = 'POST';
        
        if (exerciseId) {
            url = `/api/admin/exercises/${exerciseId}`;
            method = 'PUT';
            exercise.id = exerciseId;
        }
        
        const response = await fetch(url, {
            method: method,
            headers: getAuthHeaders(),
            body: JSON.stringify(exercise)
        });
        
        if (!response.ok) {
            throw new Error('Failed to save exercise');
        }
        
        exerciseModal.hide();
        loadExercises(currentLessonId);
        showSuccess('Exercise saved successfully');
        
    } catch (error) {
        console.error('Error saving exercise:', error);
        showError('Failed to save exercise');
    }
}

// Edit exercise
async function editExercise(exerciseId) {
    try {
        // We need to get the exercise details
        // This endpoint might need to be created
        const response = await fetch(`/api/admin/exercises/${exerciseId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to load exercise');
        }
        
        const exercise = await response.json();
        
        document.getElementById('exercise-id').value = exercise.id;
        document.getElementById('exercise-question').value = exercise.question;
        document.getElementById('exercise-hint').value = exercise.hint || '';
        document.getElementById('exercise-order').value = exercise.exerciseOrder;
        document.getElementById('exercise-xp-reward').value = exercise.xpReward;
        document.getElementById('exercise-hearts-cost').value = exercise.heartsCost;
        
        // Set exercise type
        const exerciseType = exercise.type || getExerciseType(exercise);
        document.getElementById('exercise-type').value = exerciseType;
        
        // Hide all exercise type fields
        document.querySelectorAll('.exercise-type-fields').forEach(field => {
            field.classList.add('d-none');
        });
        
        // Show fields for selected type and populate
        if (exerciseType === 'TRANSLATION') {
            document.getElementById('translation-fields').classList.remove('d-none');
            document.getElementById('translation-correct-answer').value = exercise.correctAnswer;
        } else if (exerciseType === 'MULTIPLE_CHOICE') {
            document.getElementById('multiple-choice-fields').classList.remove('d-none');
            
            // Clear existing options
            document.getElementById('options-container').innerHTML = '';
            
            // Add options
            exercise.options.forEach((option, index) => {
                addMultipleChoiceOption(option, index === exercise.correctOptionIndex);
            });
        } else if (exerciseType === 'MATCHING') {
            document.getElementById('matching-fields').classList.remove('d-none');
            
            // Clear existing pairs
            document.getElementById('pairs-container').innerHTML = '';
            
            // Add pairs
            for (const [key, value] of Object.entries(exercise.pairs)) {
                addMatchingPair(key, value);
            }
        }
        
        document.getElementById('exerciseModalLabel').textContent = 'Edit Exercise';
        exerciseModal.show();
        
    } catch (error) {
        console.error('Error loading exercise:', error);
        showError('Failed to load exercise');
    }
}

// Delete exercise
async function deleteExercise(exerciseId) {
    if (!confirm('Are you sure you want to delete this exercise? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/admin/exercises/${exerciseId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        
        if (!response.ok) {
            throw new Error('Failed to delete exercise');
        }
        
        loadExercises(currentLessonId);
        showSuccess('Exercise deleted successfully');
        
    } catch (error) {
        console.error('Error deleting exercise:', error);
        showError('Failed to delete exercise');
    }
}

// Add multiple choice option
function addMultipleChoiceOption(value = '', isCorrect = false) {
    const optionsContainer = document.getElementById('options-container');
    const optionIndex = optionsContainer.children.length;
    
    const optionDiv = document.createElement('div');
    optionDiv.className = 'input-group mb-2';
    optionDiv.innerHTML = `
        <div class="input-group-text">
            <input class="form-check-input mt-0 option-radio" type="radio" name="correctOption" value="${optionIndex}" ${isCorrect ? 'checked' : ''}>
        </div>
        <input type="text" class="form-control option-input" placeholder="Option ${optionIndex + 1}" value="${value}">
        <button class="btn btn-outline-danger remove-option" type="button">Remove</button>
    `;
    
    optionsContainer.appendChild(optionDiv);
    
    // Add event listener to remove button
    optionDiv.querySelector('.remove-option').addEventListener('click', () => {
        optionDiv.remove();
        
        // Update option indices
        const options = optionsContainer.querySelectorAll('.option-radio');
        options.forEach((option, index) => {
            option.value = index;
        });
    });
}

// Add matching pair
function addMatchingPair(key = '', value = '') {
    const pairsContainer = document.getElementById('pairs-container');
    
    const pairDiv = document.createElement('div');
    pairDiv.className = 'input-group mb-2';
    pairDiv.innerHTML = `
        <input type="text" class="form-control pair-key" placeholder="Key" value="${key}">
        <input type="text" class="form-control pair-value" placeholder="Value" value="${value}">
        <button class="btn btn-outline-danger remove-pair" type="button">Remove</button>
    `;
    
    pairsContainer.appendChild(pairDiv);
    
    // Add event listener to remove button
    pairDiv.querySelector('.remove-pair').addEventListener('click', () => {
        pairDiv.remove();
    });
}

// Promote user to admin
async function promoteToAdmin() {
    const username = document.getElementById('username-input').value;
    
    if (!username) {
        showError('Please enter a username');
        return;
    }
    
    try {
        const response = await fetch('/api/admin/users/create-admin', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ username })
        });
        
        if (!response.ok) {
            throw new Error('Failed to promote user');
        }
        
        showSuccess(`User ${username} promoted to admin successfully`);
        document.getElementById('username-input').value = '';
        
    } catch (error) {
        console.error('Error promoting user:', error);
        showError('Failed to promote user');
    }
}

// Show success message
function showSuccess(message) {
    alert(message); // Replace with a better UI notification
}

// Show error message
function showError(message) {
    alert(message); // Replace with a better UI notification
}