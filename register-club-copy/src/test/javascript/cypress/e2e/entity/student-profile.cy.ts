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

describe('StudentProfile e2e test', () => {
  const studentProfilePageUrl = '/student-profile';
  const studentProfilePageUrlPattern = new RegExp('/student-profile(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const studentProfileSample = { studentId: 'longingly because deeply', fullName: 'prudent', grade: 'quarterly afore' };

  let studentProfile;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/student-profiles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/student-profiles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/student-profiles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (studentProfile) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/student-profiles/${studentProfile.id}`,
      }).then(() => {
        studentProfile = undefined;
      });
    }
  });

  it('StudentProfiles menu should load StudentProfiles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('student-profile');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StudentProfile').should('exist');
    cy.url().should('match', studentProfilePageUrlPattern);
  });

  describe('StudentProfile page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(studentProfilePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StudentProfile page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/student-profile/new$'));
        cy.getEntityCreateUpdateHeading('StudentProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentProfilePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/student-profiles',
          body: studentProfileSample,
        }).then(({ body }) => {
          studentProfile = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/student-profiles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [studentProfile],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(studentProfilePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StudentProfile page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('studentProfile');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentProfilePageUrlPattern);
      });

      it('edit button click should load edit StudentProfile page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StudentProfile');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentProfilePageUrlPattern);
      });

      it('edit button click should load edit StudentProfile page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StudentProfile');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentProfilePageUrlPattern);
      });

      it('last delete button click should delete instance of StudentProfile', () => {
        cy.intercept('GET', '/api/student-profiles/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('studentProfile').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', studentProfilePageUrlPattern);

        studentProfile = undefined;
      });
    });
  });

  describe('new StudentProfile page', () => {
    beforeEach(() => {
      cy.visit(`${studentProfilePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StudentProfile');
    });

    it('should create an instance of StudentProfile', () => {
      cy.get(`[data-cy="studentId"]`).type('psst range though');
      cy.get(`[data-cy="studentId"]`).should('have.value', 'psst range though');

      cy.get(`[data-cy="fullName"]`).type('cycle');
      cy.get(`[data-cy="fullName"]`).should('have.value', 'cycle');

      cy.get(`[data-cy="grade"]`).type('weary');
      cy.get(`[data-cy="grade"]`).should('have.value', 'weary');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        studentProfile = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', studentProfilePageUrlPattern);
    });
  });
});
