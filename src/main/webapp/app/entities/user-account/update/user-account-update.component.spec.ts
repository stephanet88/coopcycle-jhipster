jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { UserAccountService } from '../service/user-account.service';
import { IUserAccount, UserAccount } from '../user-account.model';

import { UserAccountUpdateComponent } from './user-account-update.component';

describe('Component Tests', () => {
  describe('UserAccount Management Update Component', () => {
    let comp: UserAccountUpdateComponent;
    let fixture: ComponentFixture<UserAccountUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let userAccountService: UserAccountService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UserAccountUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(UserAccountUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserAccountUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      userAccountService = TestBed.inject(UserAccountService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const userAccount: IUserAccount = { id: 456 };

        activatedRoute.data = of({ userAccount });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(userAccount));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const userAccount = { id: 123 };
        spyOn(userAccountService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ userAccount });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: userAccount }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(userAccountService.update).toHaveBeenCalledWith(userAccount);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const userAccount = new UserAccount();
        spyOn(userAccountService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ userAccount });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: userAccount }));
        saveSubject.complete();

        // THEN
        expect(userAccountService.create).toHaveBeenCalledWith(userAccount);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const userAccount = { id: 123 };
        spyOn(userAccountService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ userAccount });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(userAccountService.update).toHaveBeenCalledWith(userAccount);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
