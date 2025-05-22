import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('StudentClubRegistration e2e test', () => {
  const studentClubRegistrationPageUrl = '/student-club-registration';
  const studentClubRegistrationPageUrlPattern = new RegExp('/student-club-registration(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const studentClubRegistrationSample = { registrationDate: '2025-05-22T01:53:21.550Z', status: 'REJECTED' };

  let studentClubRegistration;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/student-club-registrations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/student-club-registrations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/student-club-registrations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (studentClubRegistration) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/student-club-registrations/${studentClubRegistration.id}`,
      }).then(() => {
        studentClubRegistration = undefined;
      });
    }
  });

  it('StudentClubRegistrations menu should load StudentClubRegistrations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('student-club-registration');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StudentClubRegistration').should('exist');
    cy.url().should('match', studentClubRegistrationPageUrlPattern);
  });

  describe('StudentClubRegistration page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(studentClubRegistrationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StudentClubRegistration page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/student-club-registration/new$'));
        cy.getEntityCreateUpdateHeading('StudentClubRegistration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentClubRegistrationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/student-club-registrations',
          body: studentClubRegistrationSample,
        }).then(({ body }) => {
          studentClubRegistration = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/student-club-registrations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/student-club-registrations?page=0&size=20>; rel="last",<http://localhost/api/student-club-registrations?page=0&size=20>; rel="first"',
              },
              body: [studentClubRegistration],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(studentClubRegistrationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StudentClubRegistration page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('studentClubRegistration');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentClubRegistrationPageUrlPattern);
      });

      it('edit button click should load edit StudentClubRegistration page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StudentClubRegistration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentClubRegistrationPageUrlPattern);
      });

      it('edit button click should load edit StudentClubRegistration page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StudentClubRegistration');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentClubRegistrationPageUrlPattern);
      });

      it('last delete button click should delete instance of StudentClubRegistration', () => {
        cy.intercept('GET', '/api/student-club-registrations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('studentClubRegistration').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentClubRegistrationPageUrlPattern);

        studentClubRegistration = undefined;
      });
    });
  });

  describe('new StudentClubRegistration page', () => {
    beforeEach(() => {
      cy.visit(`${studentClubRegistrationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StudentClubRegistration');
    });

    it('should create an instance of StudentClubRegistration', () => {
      cy.get(`[data-cy="registrationDate"]`).type('2025-05-21T09:16');
      cy.get(`[data-cy="registrationDate"]`).blur();
      cy.get(`[data-cy="registrationDate"]`).should('have.value', '2025-05-21T09:16');

      cy.get(`[data-cy="status"]`).select('APPROVED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        studentClubRegistration = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', studentClubRegistrationPageUrlPattern);
    });
  });
});
