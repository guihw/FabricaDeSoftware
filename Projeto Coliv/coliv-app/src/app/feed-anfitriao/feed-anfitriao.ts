import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ColegasCardComponent } from './components/colegas-card-component/colegas-card-component';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';

@Component({
  selector: 'app-feed-anfitriao',
  imports: [RouterOutlet, RouterLink, ColegasCardComponent, TopNavbarComponent],
  templateUrl: './feed-anfitriao.html',
  styleUrl: './feed-anfitriao.css',
})
export class FeedAnfitriao {

}
