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

describe('Club e2e test', () => {
  const clubPageUrl = '/club';
  const clubPageUrlPattern = new RegExp('/club(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const clubSample = { name: 'against regarding' };

  let club;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/clubs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/clubs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/clubs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (club) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/clubs/${club.id}`,
      }).then(() => {
        club = undefined;
      });
    }
  });

  it('Clubs menu should load Clubs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('club');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Club').should('exist');
    cy.url().should('match', clubPageUrlPattern);
  });

  describe('Club page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(clubPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Club page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/club/new$'));
        cy.getEntityCreateUpdateHeading('Club');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/clubs',
          body: clubSample,
        }).then(({ body }) => {
          club = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/clubs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [club],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(clubPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Club page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('club');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);
      });

      it('edit button click should load edit Club page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Club');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);
      });

      it('edit button click should load edit Club page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Club');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);
      });

      it('last delete button click should delete instance of Club', () => {
        cy.intercept('GET', '/api/clubs/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('club').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);

        club = undefined;
      });
    });
  });

  describe('new Club page', () => {
    beforeEach(() => {
      cy.visit(`${clubPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Club');
    });

    it('should create an instance of Club', () => {
      cy.get(`[data-cy="name"]`).type('whereas clumsy glimmer');
      cy.get(`[data-cy="name"]`).should('have.value', 'whereas clumsy glimmer');

      cy.get(`[data-cy="description"]`).type('round');
      cy.get(`[data-cy="description"]`).should('have.value', 'round');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        club = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', clubPageUrlPattern);
    });
  });
});
