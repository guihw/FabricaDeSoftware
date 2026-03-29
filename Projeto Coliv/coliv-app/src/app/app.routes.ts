import { Register } from './features/register/register';
import { Routes } from '@angular/router';
import { Home } from './features/home/home';

export const routes: Routes = [
  {path: '', component: Home},
  {path: 'home', component: Home},
  {path: 'register', component: Register, title: 'Register Page'}
];
