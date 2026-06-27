import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Despesas } from './despesas/despesas';
import { Cadastro } from './cadastro/cadastro';
import { FormPreferencias } from './form-preferencias/form-preferencias';
import { FeedAnfitriao } from './feed-anfitriao/feed-anfitriao';
import { FeedColega } from './feed-colega/feed-colega';
import { CriarAnuncio } from './criar-anuncio/criar-anuncio';
import { GerenciarAnuncios } from './gerenciar-anuncios/gerenciar-anuncios';
import { Chat } from './chat/chat';
import { Perfil } from './perfil/perfil';
import { authGuard } from './core/guards/auth-guard-guard';
import { despesasGuard } from './core/guards/despesas-guard';
import { anfitriaoGuard, colegaGuard } from './core/guards/role-guard-guard';
import { anuncioGuard } from './core/guards/anuncio-guard-guard';
import { Login } from './login/login';
import { Ajuda } from './ajuda/ajuda';



export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'cadastro', component: Cadastro },
  { path: 'ajuda', component: Ajuda },

  { path: 'feedcolega', component: FeedColega, canActivate: [colegaGuard] },
  { path: 'feedanfitriao', component: FeedAnfitriao, canActivate: [anfitriaoGuard, anuncioGuard] },
  { path: 'criaranuncio', component: CriarAnuncio, canActivate: [anfitriaoGuard] },
  { path: 'gerenciaranuncios', component: GerenciarAnuncios, canActivate: [anfitriaoGuard] },
  { path: 'preferencias', component: FormPreferencias, canActivate: [authGuard] },
  { path: 'despesas', component: Despesas, canActivate: [authGuard, despesasGuard] },
  { path: 'chat/:matchId', component: Chat, canActivate: [authGuard] },
  { path: 'perfil', component: Perfil, canActivate: [authGuard] },
];