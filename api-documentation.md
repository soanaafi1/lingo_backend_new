# Duolingo API Documentation

This document provides a comprehensive guide to all API endpoints available in the Duolingo application, along with sample requests and responses for testing.

## Table of Contents

- [Authentication](#authentication)
- [Courses](#courses)
- [Lessons](#lessons)
- [Exercises](#exercises)
- [Progress](#progress)
- [Streak](#streak)
- [Friends](#friends)
- [Leaderboard](#leaderboard)
- [Admin](#admin)

## Authentication

### Login

**Endpoint:** `POST /api/auth/login`

**Description:** Authenticates a user and returns a JWT token.

**Request:**
```json
{
  "emmail": "user@email.com",
  "password": "password123"
}
```

**Response:**
Access token expires in 24 hours and refresh token in 7 days, unless used (refresh token) or revoked (access token) before then
```json
{
  "id": "user_id",
  "accessToken": "access_token",
  "refreshToken": "refresh_token",
  "role": "ADMIN",
  "authorities": [
    "ADMIN"
  ]
}
```

### Register

**Endpoint:** `POST /api/auth/register`

**Description:** Registers a new user.

**Request:**
```json
{
  "fullName": "newuser",
  "email": "user@example.com",
  "password": "securePassword123",
  "language": {
    "SPANISH": "BEGINNER"
  },
  "age": 30,
  "role": "USER"
}
```

**Response:**
```json
"User registered successfully"
```

### Update Avatar

**Endpoint:** `PUT /api/auth/update-avatar`

**Description:** Updates the user's avatar URL.

**Request:**
```json
{
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "testuser",
  "email": "test@example.com",
  "xpPoints": 150,
  "streak": 5,
  "hearts": 5,
  "role": "USER",
  "authorities": ["USER"],
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

## Courses

### Get All Courses

**Endpoint:** `GET /api/courses`

**Description:** Returns a list of all available courses.

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Spanish for English Speakers",
    "description": "Learn Spanish from English",
    "sourceLanguage": "English",
    "targetLanguage": "Spanish",
    "difficulty": "BEGINNER",
    "imageUrl": "https://example.com/spanish.jpg",
    "lessons": []
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "French for English Speakers",
    "description": "Learn French from English",
    "sourceLanguage": "English",
    "targetLanguage": "French",
    "difficulty": "BEGINNER",
    "imageUrl": "https://example.com/french.jpg",
    "lessons": []
  }
]
```

### Get Course by ID

**Endpoint:** `GET /api/courses/{id}`

**Description:** Returns a specific course by ID, including its lessons.

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Spanish for English Speakers",
  "description": "Learn Spanish from English",
  "sourceLanguage": "English",
  "targetLanguage": "Spanish",
  "difficulty": "BEGINNER",
  "imageUrl": "https://example.com/spanish.jpg",
  "lessons": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "name": "Basics 1",
      "description": "Learn basic Spanish phrases",
      "order": 1
    }
  ]
}
```

### Get Courses by Language

**Endpoint:** `GET /api/courses/language/{language}`

**Description:** Returns courses for a specific target language.

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Spanish for English Speakers",
    "description": "Learn Spanish from English",
    "sourceLanguage": "English",
    "targetLanguage": "Spanish",
    "difficulty": "BEGINNER",
    "imageUrl": "https://example.com/spanish.jpg",
    "lessons": []
  }
]
```

## Lessons

### Get Lessons by Course

**Endpoint:** `GET /api/lessons/{courseId}`

**Description:** Returns all lessons for a specific course.

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "Basics 1",
    "description": "Learn basic Spanish phrases",
    "order": 1
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "name": "Basics 2",
    "description": "Continue learning basic Spanish",
    "order": 2
  }
]
```

## Exercises

### Get Exercises by Lesson

**Endpoint:** `GET /api/exercises/lesson/{lessonId}`

**Description:** Returns all exercises for a specific lesson.

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440005",
    "type": "translation",
    "question": "Translate 'Hello' to Spanish",
    "hint": "It starts with 'H'",
    "order": 1,
    "xpReward": 10,
    "heartsCost": 1,
    "correctAnswer": "Hola"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440006",
    "type": "multiple_choice",
    "question": "Which word means 'cat' in Spanish?",
    "hint": "It's similar to 'gate'",
    "order": 2,
    "xpReward": 10,
    "heartsCost": 1,
    "options": ["Perro", "Gato", "Pájaro", "Ratón"],
    "correctOptionIndex": 1,
    "correctAnswer": "Gato"
  }
]
```

### Create Exercise

**Endpoint:** `POST /api/exercises/lesson/{lessonId}`

**Description:** Creates a new exercise for a specific lesson.

**Request (Translation Exercise):**
```json
{
  "type": "translation",
  "question": "Translate 'Goodbye' to Spanish",
  "hint": "It starts with 'A'",
  "order": 3,
  "xpReward": 10,
  "heartsCost": 1,
  "correctAnswer": "Adiós"
}
```

**Request (Multiple Choice Exercise):**
```json
{
  "type": "multiple_choice",
  "question": "Which word means 'dog' in Spanish?",
  "hint": "It starts with 'P'",
  "order": 4,
  "xpReward": 10,
  "heartsCost": 1,
  "options": ["Gato", "Perro", "Pájaro", "Ratón"],
  "correctOptionIndex": 1
}
```

**Request (Matching Exercise):**
```json
{
  "type": "matching",
  "question": "Match the Spanish words with their English translations",
  "hint": "Think about similar words",
  "order": 5,
  "xpReward": 15,
  "heartsCost": 1,
  "pairs": {
    "Gato": "Cat",
    "Perro": "Dog",
    "Pájaro": "Bird"
  }
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440007",
  "type": "translation",
  "question": "Translate 'Goodbye' to Spanish",
  "hint": "It starts with 'A'",
  "order": 3,
  "xpReward": 10,
  "heartsCost": 1,
  "correctAnswer": "Adiós"
}
```

## Progress

### Submit Exercise

**Endpoint:** `POST /api/progress/submit?exerciseId={exerciseId}`

**Description:** Submits an answer for an exercise and returns the progress.

**Request:**
```json
{
  "answer": "Hola"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440008",
  "exerciseId": "550e8400-e29b-41d4-a716-446655440005",
  "exerciseType": "TranslationExercise",
  "question": "Translate 'Hello' to Spanish",
  "completed": true,
  "correct": true,
  "completedAt": "2023-06-02T15:30:45",
  "userAnswer": "Hola",
  "xpEarned": 10,
  "heartsUsed": 0
}
```

### Get User Progress

**Endpoint:** `GET /api/progress`

**Description:** Returns all progress entries for the current user.

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440008",
    "exerciseId": "550e8400-e29b-41d4-a716-446655440005",
    "exerciseType": "TranslationExercise",
    "question": "Translate 'Hello' to Spanish",
    "completed": true,
    "correct": true,
    "completedAt": "2023-06-02T15:30:45",
    "userAnswer": "Hola",
    "xpEarned": 10,
    "heartsUsed": 0
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440009",
    "exerciseId": "550e8400-e29b-41d4-a716-446655440006",
    "exerciseType": "MultipleChoiceExercise",
    "question": "Which word means 'cat' in Spanish?",
    "completed": true,
    "correct": false,
    "completedAt": "2023-06-02T15:31:20",
    "userAnswer": "Perro",
    "xpEarned": 0,
    "heartsUsed": 1
  }
]
```

### Get Lesson Progress

**Endpoint:** `GET /api/progress/lesson/{lessonId}`

**Description:** Returns the progress for a specific lesson.

**Response:**
```json
{
  "lessonId": "550e8400-e29b-41d4-a716-446655440002",
  "totalExercises": 5,
  "completedExercises": 2,
  "correctExercises": 1,
  "percentComplete": 40,
  "percentCorrect": 50
}
```

## Streak

### Get Streak

**Endpoint:** `GET /api/streak`

**Description:** Returns the user's streak information.

**Response:**
```json
{
  "currentStreak": 5,
  "lastStreakUpdate": "2023-06-02T10:15:30",
  "streakFreezeCount": 2,
  "practicedToday": true
}
```

### Use Streak Freeze

**Endpoint:** `POST /api/streak/freeze`

**Description:** Uses a streak freeze to maintain the streak.

**Response:**
```
200 OK
```

### Buy Streak Freeze

**Endpoint:** `POST /api/streak/buy-freeze`

**Description:** Buys a streak freeze using XP points.

**Response:**
```
200 OK
```

## Friends

### Get Friends

**Endpoint:** `GET /api/friends`

**Description:** Returns the user's friends.

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440010",
    "userId": "550e8400-e29b-41d4-a716-446655440011",
    "username": "friend1",
    "xpPoints": 200,
    "streak": 7,
    "accepted": true,
    "isPending": false,
    "isIncoming": false,
    "createdAt": "2023-05-15T14:30:00",
    "acceptedAt": "2023-05-15T15:00:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440012",
    "userId": "550e8400-e29b-41d4-a716-446655440013",
    "username": "friend2",
    "xpPoints": 150,
    "streak": 3,
    "accepted": true,
    "isPending": false,
    "isIncoming": true,
    "createdAt": "2023-05-20T10:15:00",
    "acceptedAt": "2023-05-20T11:00:00"
  }
]
```

### Get Pending Friend Requests

**Endpoint:** `GET /api/friends/pending`

**Description:** Returns pending friend requests (both sent and received).

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440014",
    "userId": "550e8400-e29b-41d4-a716-446655440015",
    "username": "pendingfriend1",
    "xpPoints": 120,
    "streak": 2,
    "accepted": false,
    "isPending": true,
    "isIncoming": true,
    "createdAt": "2023-06-01T09:45:00",
    "acceptedAt": null
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440016",
    "userId": "550e8400-e29b-41d4-a716-446655440017",
    "username": "pendingfriend2",
    "xpPoints": 180,
    "streak": 4,
    "accepted": false,
    "isPending": true,
    "isIncoming": false,
    "createdAt": "2023-06-01T14:20:00",
    "acceptedAt": null
  }
]
```

### Send Friend Request

**Endpoint:** `POST /api/friends/request`

**Description:** Sends a friend request to another user.

**Request:**
```json
{
  "username": "usertofriend"
}
```

**Response:**
```
200 OK
```

### Accept Friend Request

**Endpoint:** `POST /api/friends/accept/{friendshipId}`

**Description:** Accepts a friend request.

**Response:**
```
200 OK
```

### Reject or Remove Friend

**Endpoint:** `DELETE /api/friends/{friendshipId}`

**Description:** Rejects a friend request or removes a friend.

**Response:**
```
200 OK
```

### Search Users

**Endpoint:** `GET /api/friends/search?query={query}`

**Description:** Searches for users by username.

**Response:**
```json
[
  {
    "userId": "550e8400-e29b-41d4-a716-446655440018",
    "username": "searchuser1",
    "xpPoints": 90,
    "streak": 1,
    "isPending": false,
    "accepted": false,
    "isIncoming": false
  },
  {
    "userId": "550e8400-e29b-41d4-a716-446655440019",
    "username": "searchuser2",
    "xpPoints": 250,
    "streak": 10,
    "isPending": true,
    "accepted": false,
    "isIncoming": true
  }
]
```

## Leaderboard

### Get Global Leaderboard

**Endpoint:** `GET /api/leaderboard?limit={limit}`

**Description:** Returns the global leaderboard with top users by XP.

**Response:**
```json
[
  {
    "userId": "550e8400-e29b-41d4-a716-446655440020",
    "username": "topuser1",
    "xpPoints": 500,
    "streak": 15,
    "rank": 1,
    "isCurrentUser": false
  },
  {
    "userId": "550e8400-e29b-41d4-a716-446655440021",
    "username": "topuser2",
    "xpPoints": 450,
    "streak": 12,
    "rank": 2,
    "isCurrentUser": false
  },
  {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "xpPoints": 150,
    "streak": 5,
    "rank": 10,
    "isCurrentUser": true
  }
]
```

### Get User Leaderboard Position

**Endpoint:** `GET /api/leaderboard/me?range={range}`

**Description:** Returns the leaderboard centered around the current user.

**Response:**
```json
[
  {
    "userId": "550e8400-e29b-41d4-a716-446655440022",
    "username": "user8",
    "xpPoints": 180,
    "streak": 6,
    "rank": 8,
    "isCurrentUser": false
  },
  {
    "userId": "550e8400-e29b-41d4-a716-446655440023",
    "username": "user9",
    "xpPoints": 160,
    "streak": 4,
    "rank": 9,
    "isCurrentUser": false
  },
  {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "xpPoints": 150,
    "streak": 5,
    "rank": 10,
    "isCurrentUser": true
  },
  {
    "userId": "550e8400-e29b-41d4-a716-446655440024",
    "username": "user11",
    "xpPoints": 140,
    "streak": 3,
    "rank": 11,
    "isCurrentUser": false
  },
  {
    "userId": "550e8400-e29b-41d4-a716-446655440025",
    "username": "user12",
    "xpPoints": 130,
    "streak": 2,
    "rank": 12,
    "isCurrentUser": false
  }
]
```

## Admin

### Get All Courses (Admin)

**Endpoint:** `GET /api/admin/courses`

**Description:** Returns all courses (admin access).

**Response:** Same as `GET /api/courses`

### Get Course by ID (Admin)

**Endpoint:** `GET /api/admin/courses/{id}`

**Description:** Returns a specific course by ID (admin access).

**Response:** Same as `GET /api/courses/{id}`

### Create Course (Admin)

**Endpoint:** `POST /api/admin/courses`

**Description:** Creates a new course (admin access).

**Request:** Same as `POST /api/courses`

**Response:** Same as `POST /api/courses`

### Update Course (Admin)

**Endpoint:** `PUT /api/admin/courses/{id}`

**Description:** Updates a course (admin access).

**Request:**
```json
{
  "name": "Updated Spanish Course",
  "description": "Updated description",
  "sourceLanguage": "English",
  "targetLanguage": "Spanish",
  "difficulty": "INTERMEDIATE",
  "imageUrl": "https://example.com/updated-spanish.jpg"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Updated Spanish Course",
  "description": "Updated description",
  "sourceLanguage": "English",
  "targetLanguage": "Spanish",
  "difficulty": "INTERMEDIATE",
  "imageUrl": "https://example.com/updated-spanish.jpg",
  "lessons": []
}
```

### Delete Course (Admin)

**Endpoint:** `DELETE /api/admin/courses/{id}`

**Description:** Deletes a course (admin access).

**Response:**
```
204 No Content
```

### Get Lessons by Course (Admin)

**Endpoint:** `GET /api/admin/lessons/course/{courseId}`

**Description:** Returns all lessons for a specific course (admin access).

**Response:** Same as `GET /api/lessons/course/{courseId}`

### Create Lesson (Admin)

**Endpoint:** `POST /api/admin/lessons/course/{courseId}`

**Description:** Creates a new lesson for a specific course (admin access).

**Request:** Same as `POST /api/lessons/course/{courseId}`

**Response:** Same as `POST /api/lessons/course/{courseId}`

### Update Lesson (Admin)

**Endpoint:** `PUT /api/admin/lessons/{id}`

**Description:** Updates a lesson (admin access).

**Request:**
```json
{
  "name": "Updated Basics 1",
  "description": "Updated basic Spanish phrases",
  "order": 1
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "name": "Updated Basics 1",
  "description": "Updated basic Spanish phrases",
  "order": 1
}
```

### Delete Lesson (Admin)

**Endpoint:** `DELETE /api/admin/lessons/{id}`

**Description:** Deletes a lesson (admin access).

**Response:**
```
204 No Content
```

### Get Exercises by Lesson (Admin)

**Endpoint:** `GET /api/admin/exercises/lesson/{lessonId}`

**Description:** Returns all exercises for a specific lesson (admin access).

**Response:** Same as `GET /api/exercises/lesson/{lessonId}`

### Create Exercise (Admin)

**Endpoint:** `POST /api/admin/exercises/lesson/{lessonId}`

**Description:** Creates a new exercise for a specific lesson (admin access).

**Request:** Same as `POST /api/exercises/lesson/{lessonId}`

**Response:** Same as `POST /api/exercises/lesson/{lessonId}`

### Update Exercise (Admin)

**Endpoint:** `PUT /api/admin/exercises/{id}`

**Description:** Updates an exercise (admin access).

**Request:**
```json
{
  "type": "translation",
  "question": "Updated: Translate 'Hello' to Spanish",
  "hint": "Updated hint",
  "order": 1,
  "xpReward": 15,
  "heartsCost": 1,
  "correctAnswer": "Hola"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440005",
  "type": "translation",
  "question": "Updated: Translate 'Hello' to Spanish",
  "hint": "Updated hint",
  "order": 1,
  "xpReward": 15,
  "heartsCost": 1,
  "correctAnswer": "Hola"
}
```

### Delete Exercise (Admin)

**Endpoint:** `DELETE /api/admin/exercises/{id}`

**Description:** Deletes an exercise (admin access).

**Response:**
```
204 No Content
```

### Get Exercise by ID (Admin)

**Endpoint:** `GET /api/admin/exercises/{id}`

**Description:** Returns a specific exercise by ID (admin access).

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440005",
  "type": "translation",
  "question": "Translate 'Hello' to Spanish",
  "hint": "It starts with 'H'",
  "order": 1,
  "xpReward": 10,
  "heartsCost": 1,
  "correctAnswer": "Hola"
}
```

### Make User Admin

**Endpoint:** `POST /api/admin/users/make-admin`

**Description:** Promotes a user to admin role.

**Request:**
```json
{
  "username": "usertopromote"
}
```

**Response:**
```
"User promoted to admin successfully"
```

## Validate Admin Token

**Endpoint:** `GET /api/admin/validate`

**Description:** Validates an admin token.

**Response:**
```
"OK"
```