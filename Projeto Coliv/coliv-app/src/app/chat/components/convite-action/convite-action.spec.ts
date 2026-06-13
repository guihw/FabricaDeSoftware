import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConviteActionComponent } from './convite-action';


describe('ConviteAction', () => {
  let component: ConviteActionComponent;
  let fixture: ComponentFixture<ConviteActionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConviteActionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConviteActionComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
