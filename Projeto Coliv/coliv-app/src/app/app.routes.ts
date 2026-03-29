import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { PainelAnfitriaoComponent } from './features/painel-anfitriao/painel-anfitriao.component';
import { Login } from './features/login/login';

export const routes: Routes = [{
    path: '', component: Home},
    {
    path: 'anfitriao', component: PainelAnfitriaoComponent
    },
    {
        path: 'login', component: Login
    }

];
