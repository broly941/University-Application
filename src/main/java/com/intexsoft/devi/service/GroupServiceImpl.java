package com.intexsoft.devi.service;

import com.intexsoft.devi.entity.Group;
import com.intexsoft.devi.entity.Teacher;
import com.intexsoft.devi.generic.GenericServiceImpl;
import com.intexsoft.devi.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author DEVIAPHAN
 * Business Logic Service Class
 */
@Service
public class GroupServiceImpl extends GenericServiceImpl<Group> implements GroupService {

    private static final String GROUP = "group";
    private static final String GROUPS = "groups";
    private static final String UPDATE_GROUP_BY_ID = "Update group by id";
    private static final String GET_GROUP_BY_ID = "Get group by id";
    private static final String GET_BY_ID = "getById";
    private static final String GET_ALL = "getAll";
    private static final String ADD = "add";
    private static final String UPDATE_BY_ID = "updateById";
    private static final String DELETED_BY_ID = "deletedById";

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    TeacherService teacherService;

    @Autowired
    MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupServiceImpl.class);

    /**
     * @param locale of messages
     * @return getAll group entities in the database.
     */
    @Override
    public List<Group> getAll(Locale locale) {
        return getAll(groupRepository::findAll, locale, GET_ALL, GROUPS);
    }

    /**
     * @param id     of group
     * @param locale of messages
     * @return group entity by ID in the database.
     * @throws EntityNotFoundException if there is no value
     */
    @Override
    public Group getById(Long id, Locale locale) throws EntityNotFoundException {
        return getById(id, groupRepository::findById, locale, GET_BY_ID, GROUP, GET_GROUP_BY_ID);
    }

    /**
     * @param group         entity
     * @param curatorId     of teacher
     * @param teacherIdList consist of teachers
     * @param locale        of messages
     * @return added group entity in the database.
     */
    @Override
    @Transactional
    public Group save(Group group, Long curatorId, Long[] teacherIdList, Locale locale) {
        group.setTeacher(teacherService.getById(curatorId, locale));
        group.setTeachers(getTeacherList(teacherIdList, locale));
        return save(group, groupRepository::save, locale, ADD, GROUP);
    }

    /**
     * @param group         entity
     * @param groupId       of group
     * @param curatorId     of teacher
     * @param teacherIdList consist of teachers
     * @param locale        of messages
     * @return updated group entity in the database.
     * @throws EntityNotFoundException if there is no value
     */
    @Override
    @Transactional
    public Group updateById(Group group, Long groupId, Long curatorId, Long[] teacherIdList, Locale locale) throws EntityNotFoundException {
        Group currentGroup = getById(groupId, groupRepository::findById, locale, UPDATE_BY_ID, GROUP, UPDATE_GROUP_BY_ID);
        currentGroup.setNumber(group.getNumber());
        currentGroup.setTeacher(teacherService.getById(curatorId, locale));
        currentGroup.setTeachers(getTeacherList(teacherIdList, locale));
        return save(currentGroup, groupRepository::save, locale, UPDATE_BY_ID, GROUP, groupId);
    }

    /**
     * @param locale of messages
     * @param id     the group entity to be removed from the database
     */
    @Override
    public void deleteById(Long id, Locale locale) {
        deleteById(id, groupRepository::deleteById, locale, DELETED_BY_ID, GROUP);
    }

    private List<Teacher> getTeacherList(Long[] teacherIdList, Locale locale) throws EntityNotFoundException {
        return Arrays.stream(teacherIdList)
                .parallel()
                .map(id -> teacherService.getById(id, locale))
                .collect(Collectors.toList());
    }
}