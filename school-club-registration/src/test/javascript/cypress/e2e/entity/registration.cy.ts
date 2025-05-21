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

describe('Registration e2e test', () => {
  const registrationPageUrl = '/registration';
  const registrationPageUrlPattern = new RegExp('/registration(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const registrationSample = {"registrationDate":"2025-05-20T17:03:57.010Z","status":"APPROVED"};

  let registration;
  // let user;
  // let club;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"FEHYe7","firstName":"Odell","lastName":"Von-MacGyver","email":"Craig_Hyatt@hotmail.com","imageUrl":"honorable cautiously keel"},
    }).then(({ body }) => {
      user = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/clubs',
      body: {"name":"overconfidently sprinkles gracefully","description":"across however tune","maxMembers":2248},
    }).then(({ body }) => {
      club = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/registrations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/registrations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/registrations/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/clubs', {
      statusCode: 200,
      body: [club],
    });

  });
   */

  afterEach(() => {
    if (registration) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/registrations/${registration.id}`,
      }).then(() => {
        registration = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
    if (club) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/clubs/${club.id}`,
      }).then(() => {
        club = undefined;
      });
    }
  });
   */

  it('Registrations menu should load Registrations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('registration');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Registration').should('exist');
    cy.url().should('match', registrationPageUrlPattern);
  });

  describe('Registration page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(registrationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Registration page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/registration/new$'));
        cy.getEntityCreateUpdateHeading('Registration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', registrationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/registrations',
          body: {
            ...registrationSample,
            user: user,
            club: club,
          },
        }).then(({ body }) => {
          registration = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/registrations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/registrations?page=0&size=20>; rel="last",<http://localhost/api/registrations?page=0&size=20>; rel="first"',
              },
              body: [registration],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(registrationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(registrationPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Registration page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('registration');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', registrationPageUrlPattern);
      });

      it('edit button click should load edit Registration page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Registration');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', registrationPageUrlPattern);
      });

      it('edit button click should load edit Registration page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Registration');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', registrationPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Registration', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('registration').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', registrationPageUrlPattern);

        registration = undefined;
      });
    });
  });

  describe('new Registration page', () => {
    beforeEach(() => {
      cy.visit(`${registrationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Registration');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Registration', () => {
      cy.get(`[data-cy="registrationDate"]`).type('2025-05-20T17:06');
      cy.get(`[data-cy="registrationDate"]`).blur();
      cy.get(`[data-cy="registrationDate"]`).should('have.value', '2025-05-20T17:06');

      cy.get(`[data-cy="status"]`).select('APPROVED');

      cy.get(`[data-cy="user"]`).select(1);
      cy.get(`[data-cy="club"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        registration = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', registrationPageUrlPattern);
    });
  });
});
