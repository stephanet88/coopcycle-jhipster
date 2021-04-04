import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IInstitution, Institution } from '../institution.model';

import { InstitutionService } from './institution.service';

describe('Service Tests', () => {
  describe('Institution Service', () => {
    let service: InstitutionService;
    let httpMock: HttpTestingController;
    let elemDefault: IInstitution;
    let expectedResult: IInstitution | IInstitution[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(InstitutionService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        type: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Institution', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Institution()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Institution', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            type: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Institution', () => {
        const patchObject = Object.assign(
          {
            type: 'BBBBBB',
          },
          new Institution()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Institution', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            type: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Institution', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addInstitutionToCollectionIfMissing', () => {
        it('should add a Institution to an empty array', () => {
          const institution: IInstitution = { id: 123 };
          expectedResult = service.addInstitutionToCollectionIfMissing([], institution);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(institution);
        });

        it('should not add a Institution to an array that contains it', () => {
          const institution: IInstitution = { id: 123 };
          const institutionCollection: IInstitution[] = [
            {
              ...institution,
            },
            { id: 456 },
          ];
          expectedResult = service.addInstitutionToCollectionIfMissing(institutionCollection, institution);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Institution to an array that doesn't contain it", () => {
          const institution: IInstitution = { id: 123 };
          const institutionCollection: IInstitution[] = [{ id: 456 }];
          expectedResult = service.addInstitutionToCollectionIfMissing(institutionCollection, institution);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(institution);
        });

        it('should add only unique Institution to an array', () => {
          const institutionArray: IInstitution[] = [{ id: 123 }, { id: 456 }, { id: 94728 }];
          const institutionCollection: IInstitution[] = [{ id: 123 }];
          expectedResult = service.addInstitutionToCollectionIfMissing(institutionCollection, ...institutionArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const institution: IInstitution = { id: 123 };
          const institution2: IInstitution = { id: 456 };
          expectedResult = service.addInstitutionToCollectionIfMissing([], institution, institution2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(institution);
          expect(expectedResult).toContain(institution2);
        });

        it('should accept null and undefined values', () => {
          const institution: IInstitution = { id: 123 };
          expectedResult = service.addInstitutionToCollectionIfMissing([], null, institution, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(institution);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
