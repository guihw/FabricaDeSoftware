import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedAnfitriao } from './feed-anfitriao';

describe('FeedAnfitriao', () => {
  let component: FeedAnfitriao;
  let fixture: ComponentFixture<FeedAnfitriao>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeedAnfitriao]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FeedAnfitriao);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
