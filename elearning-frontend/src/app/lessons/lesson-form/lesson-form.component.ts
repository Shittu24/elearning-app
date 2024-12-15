import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { QuillEditorComponent } from 'ngx-quill';
import { LessonService } from '../lesson.service';
import { CourseService } from '../../courses/course.service';
import { Lesson } from '../lessons.model';
import { Course } from '../../courses/course.model';

// Import Quill and Delta
import QuillType from 'quill';
import Delta from 'quill';

@Component({
  selector: 'app-lesson-form',
  templateUrl: './lesson-form.component.html',
  styleUrls: ['./lesson-form.component.css']
})
export class LessonFormComponent implements OnInit {
  lessonForm: FormGroup;
  isEditMode = false;
  lessonId: number | null = null;
  courses: Course[] = [];
  selectedCourseId: number | null = null;

  // Reference to the Quill editor
  @ViewChild('quillEditor') quillEditor!: QuillEditorComponent;

  constructor(
    private fb: FormBuilder,
    private lessonService: LessonService,
    private courseService: CourseService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    // Initialize the form
    this.lessonForm = this.fb.group({
      title: ['', Validators.required],
      textContent: ['', Validators.required],
      courseId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.lessonId = +id;
      this.lessonService.getLessonById(this.lessonId).subscribe(data => {
        if (data) {
          this.lessonForm.patchValue({
            title: data.title,
            textContent: data.textContent,
            courseId: data.courseId
          });
        } else {
          this.router.navigate(['/lessons']);
        }
      });
    }

    // Load all courses for the course selection dropdown
    this.courseService.getAllCourses().subscribe(data => {
      this.courses = data;
    });

    // Set Quill editor modules configuration dynamically in ngOnInit
    if (this.quillEditor) {
      this.quillEditor.modules = {
        toolbar: {
          container: [
            ['bold', 'italic', 'underline'],  // Text formatting options
            [{ list: 'ordered' }, { list: 'bullet' }],  // Ordered and bullet lists
            ['link', 'image'],  // Link and image insert options
            [{ align: [] }],  // Text alignment
            [{ font: [] }],
            [{ size: ['small', false, 'large', 'huge'] }]  // Font size options
          ],
          handlers: {
            image: this.customImageHandler.bind(this)  // Custom image upload handler
          }
        },
        theme: 'snow',
        placeholder: 'Write lesson content here...',  // Set the placeholder
      };
    }
  }

  // Custom image handler for Quill
  customImageHandler(): void {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = () => {
      const file = input.files ? input.files[0] : null;
      if (file) {
        this.uploadImage(file);
      }
    };
  }

  // Handle image upload for the Quill editor
  uploadImage(file: File): void {
    this.lessonService.uploadFile(file, 'lesson-images').subscribe(
      (response: string) => {
        const imageUrl = response;  // Get the image URL returned by the backend
        const range = this.quillEditor.quillEditor.getSelection(true);
        this.quillEditor.quillEditor.insertEmbed(range.index, 'image', imageUrl);  // Insert the image into the editor
      },
      (error) => {
        console.error('Image upload failed:', error);
      }
    );
  }

  onSubmit(): void {
    if (this.lessonForm.valid) {
      const lesson: Lesson = {
        ...this.lessonForm.value,
        courseId: this.lessonForm.get('courseId')?.value
      };

      if (this.isEditMode && this.lessonId) {
        this.lessonService.updateLesson(this.lessonId, lesson).subscribe(() => {
          this.navigateToLessons();
        });
      } else {
        this.lessonService.createLesson(lesson).subscribe(() => {
          this.navigateToLessons();
        });
      }
    }
  }

  navigateToLessons(): void {
    this.router.navigate(['/lessons'], {
      queryParams: { course: this.selectedCourseId }
    });
  }
}