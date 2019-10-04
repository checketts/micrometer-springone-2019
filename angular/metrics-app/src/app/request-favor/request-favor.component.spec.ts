import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestFavorComponent } from './request-favor.component';

describe('RequestFavorComponent', () => {
  let component: RequestFavorComponent;
  let fixture: ComponentFixture<RequestFavorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RequestFavorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestFavorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
