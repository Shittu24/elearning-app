import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { QuillEditorComponent } from 'ngx-quill';
import { LessonService } from '../lesson.service';
import { CourseService } from '../../courses/course.service';
import { Lesson } from '../lessons.model';
import { Course } from '../../courses/course.model';

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

  @ViewChild('quillEditor', { static: false }) quillEditorComponent!: QuillEditorComponent;

  modules = {
    toolbar: {
      container: [
        ['bold', 'italic', 'underline'],
        [{ list: 'ordered' }, { list: 'bullet' }],
        ['link', 'image'],
        [{ align: [] }],
        [{ font: [] }],
        [{ size: ['small', false, 'large', 'huge'] }]
      ],
      handlers: {
        image: this.customImageHandler.bind(this)
      }
    },
    theme: 'snow',
    placeholder: 'Write lesson content here...'
  };

  constructor(
    private fb: FormBuilder,
    private lessonService: LessonService,
    private courseService: CourseService,
    private route: ActivatedRoute,
    private router: Router
  ) {
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

    this.courseService.getAllCourses().subscribe(data => {
      this.courses = data;
    });
  }

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

  uploadImage(file: File): void {
    this.lessonService.uploadFile(file, 'lesson-images').subscribe(
      (response: string) => {
        const imageUrl = response; // Backend URL for the uploaded image
        const range = this.quillEditorComponent.quillEditor.getSelection(true);
        this.quillEditorComponent.quillEditor.insertEmbed(range.index, 'image', imageUrl);
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
