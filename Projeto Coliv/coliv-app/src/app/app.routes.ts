import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Despesas } from './despesas/despesas';
import { Cadastro } from './cadastro/cadastro';
import { FormPreferencias } from './form-preferencias/form-preferencias';
import { FeedAnfitriao } from './feed-anfitriao/feed-anfitriao';


export const routes: Routes = [
  {path: '', component: Home},
  {path: 'despesas', component: Despesas}, 
  {path: 'cadastro', component: Cadastro},
  {path: 'preferencias', component: FormPreferencias},
  {path: 'feedanfitriao', component: FeedAnfitriao}
];