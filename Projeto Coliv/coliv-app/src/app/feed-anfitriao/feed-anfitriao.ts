import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ColegasCardComponent } from './components/colegas-card-component/colegas-card-component';

@Component({
  selector: 'app-feed-anfitriao',
  imports: [RouterOutlet, RouterLink, ColegasCardComponent],
  templateUrl: './feed-anfitriao.html',
  styleUrl: './feed-anfitriao.css',
})
export class FeedAnfitriao {

}
