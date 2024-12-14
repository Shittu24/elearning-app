export interface Lesson {

    id?: number;
    title?: string;
    textContent?: string;
    videoUrl?: string;
    courseId?: number;  // Matches the backend's `courseId`
    completed?: boolean;
}
