import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Despesas } from './despesas/despesas';
import { Cadastro } from './cadastro/cadastro';


export const routes: Routes = [
  {path: '', component: Home},
  {path: 'despesas', component: Despesas}, 
  {path: 'cadastro', component: Cadastro}
];