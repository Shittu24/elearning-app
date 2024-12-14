import { Lesson } from "../lessons/lessons.model";


export interface Course {
    id: number;
    title: string;
    description: string;
    imagePath?: string;  // Match the backend property name
    lessons: Lesson[];
    isEnrolled?: boolean; // New property to track enrollment status
}

