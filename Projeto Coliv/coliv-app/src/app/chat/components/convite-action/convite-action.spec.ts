import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConviteAction } from './convite-action';

describe('ConviteAction', () => {
  let component: ConviteAction;
  let fixture: ComponentFixture<ConviteAction>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConviteAction]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConviteAction);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
