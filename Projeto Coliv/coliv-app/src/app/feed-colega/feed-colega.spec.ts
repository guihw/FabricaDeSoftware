import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedColega } from './feed-colega';

describe('FeedColega', () => {
  let component: FeedColega;
  let fixture: ComponentFixture<FeedColega>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeedColega]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FeedColega);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
