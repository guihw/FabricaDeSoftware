import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Despesas } from './despesas/despesas';


export const routes: Routes = [
  {path: '', component: Home},
  {path: 'despesas', component: Despesas}, 
];