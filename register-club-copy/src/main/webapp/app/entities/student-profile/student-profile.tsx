import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './student-profile.reducer';

export const StudentProfile = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const studentProfileList = useAppSelector(state => state.studentProfile.entities);
  const loading = useAppSelector(state => state.studentProfile.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="student-profile-heading" data-cy="StudentProfileHeading">
        <Translate contentKey="registerClubApp.studentProfile.home.title">Student Profiles</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="registerClubApp.studentProfile.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/student-profile/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="registerClubApp.studentProfile.home.createLabel">Create new Student Profile</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {studentProfileList && studentProfileList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="registerClubApp.studentProfile.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('studentId')}>
                  <Translate contentKey="registerClubApp.studentProfile.studentId">Student Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('studentId')} />
                </th>
                <th className="hand" onClick={sort('fullName')}>
                  <Translate contentKey="registerClubApp.studentProfile.fullName">Full Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fullName')} />
                </th>
                <th className="hand" onClick={sort('grade')}>
                  <Translate contentKey="registerClubApp.studentProfile.grade">Grade</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('grade')} />
                </th>
                <th>
                  <Translate contentKey="registerClubApp.studentProfile.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="registerClubApp.studentProfile.studentClubRegistration">Student Club Registration</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {studentProfileList.map((studentProfile, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/student-profile/${studentProfile.id}`} color="link" size="sm">
                      {studentProfile.id}
                    </Button>
                  </td>
                  <td>{studentProfile.studentId}</td>
                  <td>{studentProfile.fullName}</td>
                  <td>{studentProfile.grade}</td>
                  <td>{studentProfile.user ? studentProfile.user.login : ''}</td>
                  <td>
                    {studentProfile.studentClubRegistration ? (
                      <Link to={`/student-club-registration/${studentProfile.studentClubRegistration.id}`}>
                        {studentProfile.studentClubRegistration.id}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/student-profile/${studentProfile.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/student-profile/${studentProfile.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/student-profile/${studentProfile.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="registerClubApp.studentProfile.home.notFound">No Student Profiles found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default StudentProfile;
