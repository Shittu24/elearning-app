import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { AboutUsComponent } from './about-us/about-us.component';

const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'about', component: AboutUsComponent },
  // { path: '**', redirectTo: '', pathMatch: 'full' },

  // Lazy loading the LessonsModule for courses' lessons
  { path: 'courses/:courseId/lessons', loadChildren: () => import('./lessons/lessons.module').then(m => m.LessonsModule) },

  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
