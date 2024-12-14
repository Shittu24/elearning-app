export interface Enrollment {
    id: number;
    userId: number;
    courseId: number;
    completed: boolean;
    courseTitle?: string;
    username?: string;
}
