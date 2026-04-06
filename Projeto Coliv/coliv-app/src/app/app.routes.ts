import { Register } from './features/register/register';
import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { Despesas } from './pages/despesas/despesas';
import { PainelAnfitriaoComponent } from './features/painel-anfitriao/painel-anfitriao.component';


export const routes: Routes = [
  {path: '', component: Home},
  {path: 'despesas', component: Despesas}, 
  {path: 'anfitriao', component: PainelAnfitriaoComponent},
  {path: 'register', component: Register, title: 'Registration Page'}
];