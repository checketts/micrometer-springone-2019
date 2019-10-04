import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FavorDetailsComponent } from './favor-details.component';

describe('FavorDetailsComponent', () => {
  let component: FavorDetailsComponent;
  let fixture: ComponentFixture<FavorDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FavorDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FavorDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
