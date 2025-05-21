import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IClub } from 'app/entities/club/club.model';
import { ClubService } from 'app/entities/club/service/club.service';
import { IRegistration } from '../registration.model';
import { RegistrationService } from '../service/registration.service';
import { RegistrationFormService } from './registration-form.service';

import { RegistrationUpdateComponent } from './registration-update.component';

describe('Registration Management Update Component', () => {
  let comp: RegistrationUpdateComponent;
  let fixture: ComponentFixture<RegistrationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let registrationFormService: RegistrationFormService;
  let registrationService: RegistrationService;
  let userService: UserService;
  let clubService: ClubService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RegistrationUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(RegistrationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RegistrationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    registrationFormService = TestBed.inject(RegistrationFormService);
    registrationService = TestBed.inject(RegistrationService);
    userService = TestBed.inject(UserService);
    clubService = TestBed.inject(ClubService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const registration: IRegistration = { id: 14380 };
      const user: IUser = { id: 3944 };
      registration.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ registration });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call Club query and add missing value', () => {
      const registration: IRegistration = { id: 14380 };
      const club: IClub = { id: 25440 };
      registration.club = club;

      const clubCollection: IClub[] = [{ id: 25440 }];
      jest.spyOn(clubService, 'query').mockReturnValue(of(new HttpResponse({ body: clubCollection })));
      const additionalClubs = [club];
      const expectedCollection: IClub[] = [...additionalClubs, ...clubCollection];
      jest.spyOn(clubService, 'addClubToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ registration });
      comp.ngOnInit();

      expect(clubService.query).toHaveBeenCalled();
      expect(clubService.addClubToCollectionIfMissing).toHaveBeenCalledWith(
        clubCollection,
        ...additionalClubs.map(expect.objectContaining),
      );
      expect(comp.clubsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const registration: IRegistration = { id: 14380 };
      const user: IUser = { id: 3944 };
      registration.user = user;
      const club: IClub = { id: 25440 };
      registration.club = club;

      activatedRoute.data = of({ registration });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.clubsSharedCollection).toContainEqual(club);
      expect(comp.registration).toEqual(registration);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRegistration>>();
      const registration = { id: 10394 };
      jest.spyOn(registrationFormService, 'getRegistration').mockReturnValue(registration);
      jest.spyOn(registrationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ registration });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: registration }));
      saveSubject.complete();

      // THEN
      expect(registrationFormService.getRegistration).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(registrationService.update).toHaveBeenCalledWith(expect.objectContaining(registration));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRegistration>>();
      const registration = { id: 10394 };
      jest.spyOn(registrationFormService, 'getRegistration').mockReturnValue({ id: null });
      jest.spyOn(registrationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ registration: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: registration }));
      saveSubject.complete();

      // THEN
      expect(registrationFormService.getRegistration).toHaveBeenCalled();
      expect(registrationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRegistration>>();
      const registration = { id: 10394 };
      jest.spyOn(registrationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ registration });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(registrationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareClub', () => {
      it('should forward to clubService', () => {
        const entity = { id: 25440 };
        const entity2 = { id: 10245 };
        jest.spyOn(clubService, 'compareClub');
        comp.compareClub(entity, entity2);
        expect(clubService.compareClub).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
