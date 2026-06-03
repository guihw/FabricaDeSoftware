import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Despesas } from './despesas/despesas';
import { Cadastro } from './cadastro/cadastro';
import { FormPreferencias } from './form-preferencias/form-preferencias';
import { FeedAnfitriao } from './feed-anfitriao/feed-anfitriao';
import { FeedColega } from './feed-colega/feed-colega';
import { CriarAnuncio } from './criar-anuncio/criar-anuncio';
import { Chat } from './chat/chat';


export const routes: Routes = [
  {path: '', component: Home},
  {path: 'despesas', component: Despesas}, 
  {path: 'cadastro', component: Cadastro},
  {path: 'preferencias', component: FormPreferencias},
  {path: 'feedanfitriao', component: FeedAnfitriao},
  {path: 'feedcolega', component: FeedColega},
  {path: 'criaranuncio', component: CriarAnuncio},
  {path: 'chat/:matchId',  component: Chat}
];