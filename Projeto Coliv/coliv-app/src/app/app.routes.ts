import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { Despesas } from './pages/despesas/despesas';

export const routes: Routes = [{
    path: '', component: Home
},{
    path: 'despesas', component: Despesas
}];
