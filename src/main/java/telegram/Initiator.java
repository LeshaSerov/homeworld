package telegram;

import repository.Database;
import telegram.domain.Access;
import telegram.domain.MemberData;
import telegram.domain.State;
import telegram.domain.StateMachine;
import telegram.operators.OperatorsWhichGeneratesKeyboard;
import telegram.operators.OperatorsWhichRunsAtStartup;
import telegram.operators.OperatorsWhoProcessesMessages;
import util.ConnectionPool.ConnectionPool;

public class Initiator {

    public static Boolean reset(ConnectionPool connector) {
        try {
            Boolean a1 = Database.recreateDatabase(connector);
            Boolean a2 = Database.createAllRoles(connector);
            Boolean a3 = Database.createResourceChat(connector);
            return a1 && a2 && a3;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static State initializeStateMachine() {
        //Объявление машины состояний
        StateMachine stateMachine = new StateMachine(
                "Default",
                "Стандартное состояние"
        );

        stateMachine.addPath(
                "SelectProject",
                "Группу необходимо \nзакрепить за проектом, \nдля этого создайте свой проект \nили выберите из списка\\.",
                "Добавить Группу",
                Access.Levels.NoNe
        ).next("SelectProject");
        {
            stateMachine.addPathProcessesMessages(
                    OperatorsWhoProcessesMessages::addProject,
                    "AddProject",
                    "Введите название проекта:",
                    null,
                    Access.Levels.NoNe
            );
            stateMachine.relocationPathInPathGenerateKeyboard(
                    "ListChats",
                    "Выберите чат \\- который будет считаться основным\\(чатом администрации\\) в добавляемом проекте\\." +
                            " Каждый участник чата будет иметь возможность подключать свои группы к проекту\\." +
                            " Внимание\\! Чат изменить будет невозможно\\." +
                            "Внимание\\! Право удалить или изменить проект будут иметь все участники чата\\.",
                    "Добавить проект",
                    OperatorsWhichGeneratesKeyboard::listChatsUnlinkedProjects,
                    MemberData.TypeReceivedInformation.IdChat,
                    Access.Levels.NoNe,
                    "AddProject"
            );

            stateMachine.addPathGenerateKeyboard(
                    "ListCreatedProjects",
                    "Тут должно быть описание",
                    "Редактировать проект",
                    OperatorsWhichGeneratesKeyboard::listProjects,
                    MemberData.TypeReceivedInformation.IdProject,
                    Access.Levels.NoNe,
                    "Project",
                    "Тут должно быть описание",
                    Access.Levels.NoNe
            ).next("ListCreatedProjects").next("Project");
            {
                stateMachine.addPathProcessesMessages(
                        OperatorsWhoProcessesMessages::editProject,
                        "EditProject",
                        "Тут должно быть описание",
                        "Изменить Проект",
                        Access.Levels.NoNe
                );

                stateMachine.addPathRunAtStartup(
                        OperatorsWhichRunsAtStartup::deleteProject,
                        "DeleteProject",
                        "Удалить Проект",
                        Access.Levels.NoNe
                );

                stateMachine.previous().previous();
            }

            stateMachine.addPathProcessesMessages(
                    OperatorsWhoProcessesMessages::addGroup,
                    "AddGroup",
                    "Введите название группы",
                    "Добавить Группу",
                    Access.Levels.NoNe
            );
            stateMachine.relocationPathInPathGenerateKeyboard(
                    "ListProjects",
                    "Тут должно быть описание",
                    "Выбрать Проект",
                    OperatorsWhichGeneratesKeyboard::listProjects,
                    MemberData.TypeReceivedInformation.IdProject,
                    Access.Levels.NoNe,
                    "AddGroup"
            );

            stateMachine.previous();
        }

        stateMachine.addPathGenerateKeyboard(
                "ListGroup",
                "Выберите группу из списка",
                "Список Групп",
                OperatorsWhichGeneratesKeyboard::listGroup,
                MemberData.TypeReceivedInformation.IdGroup,
                Access.Levels.NoNe,
                "Group",
                "Тут должно быть описание",
                Access.Levels.NoNe
        ).next("ListGroup").next("Group");
        {
            //Состояние Group

            stateMachine.addPathGenerateKeyboard(
                    "ListMembersGroup",
                    "Выберите участника из списка",
                    "Список участников группы",
                    OperatorsWhichGeneratesKeyboard::listMembersGroup,
                    MemberData.TypeReceivedInformation.IdOtherMember,
                    Access.Levels.RightAdmin,
                    "Member",
                    "Тут должно быть описание",
                    Access.Levels.RightAdmin
            ).next("ListMembersGroup").next("Member");
            {
                //Состояние Member

                stateMachine.addPathRunAtStartup(
                        OperatorsWhichRunsAtStartup::deleteMember,
                        "DeleteMember",
                        "Удалить участника",
                        Access.Levels.RightAdmin
                );

                stateMachine.addPathRunAtStartup(
                        OperatorsWhichRunsAtStartup::editRole,
                        "ChangeRole",
                        null,
                        Access.Levels.RightAdmin
                );
                stateMachine.relocationPathInPathGenerateKeyboard(
                        "ListRoles",
                        "Тут должно быть описание",
                        "Изменить Роль",
                        OperatorsWhichGeneratesKeyboard::listRoles,
                        MemberData.TypeReceivedInformation.IdRoleOtherMember,
                        Access.Levels.RightAdmin,
                        "ChangeRole"
                );

                stateMachine.addPathGenerateKeyboard(
                        "ListWarnings",
                        "Тут должно быть описание",
                        "Список предупреждений",
                        OperatorsWhichGeneratesKeyboard::listWarns,
                        MemberData.TypeReceivedInformation.idWarning,
                        Access.Levels.RightAdmin,
                        "Warning",
                        "Тут должно быть описание",
                        Access.Levels.RightAdmin
                ).next("ListWarnings").next("Warning");
                {

                    stateMachine.addPathProcessesMessages(
                            OperatorsWhoProcessesMessages::editWarning,
                            "EditWarning",
                            "Тут должно быть описание",
                            "Изменить Предупреждение",
                            Access.Levels.RightAdmin
                    );

                    stateMachine.addPathRunAtStartup(
                            OperatorsWhichRunsAtStartup::deleteWarning,
                            "DeleteWarning",
                            "Удалить Предупреждение",
                            Access.Levels.RightAdmin
                    );

                    stateMachine.previous().previous();
                }

                stateMachine.addPathProcessesMessages(
                        OperatorsWhoProcessesMessages::addWarning,
                        "AddWarning",
                        "Введите причину предупреждения",
                        "Добавить Предупреждение",
                        Access.Levels.RightAdmin
                );

                stateMachine.previous().previous();
            }

            stateMachine.addPathRunAtStartup(
                    OperatorsWhichRunsAtStartup::addMember,
                    "AddMember",
                    null,
                    Access.Levels.RightAdmin
            );
            stateMachine.relocationPathInPathGenerateKeyboard(
                    "ListNonMembers",
                    "Нажмите на пользователя, чтобы добавить его в группу",
                    "Добавить участников в группу",
                    OperatorsWhichGeneratesKeyboard::listNonMembers,
                    MemberData.TypeReceivedInformation.IdOtherMember,
                    Access.Levels.RightAdmin,
                    "AddMember"
            );

            stateMachine.addPathRunAtStartup(
                    OperatorsWhichRunsAtStartup::Ping,
                    "Ping",
                    null,
                    Access.Levels.RightPing
            );
            stateMachine.relocationPathInPathGenerateKeyboard(
                    "ListChats",
                    "Тут должно быть описание",
                    "Пинг",
                    OperatorsWhichGeneratesKeyboard::listChats,
                    MemberData.TypeReceivedInformation.IdChat,
                    Access.Levels.RightEdit,
                    "Ping"
            );

            stateMachine.addPath(
                    "FileSystem",
                    "Работа с файлами",
                    "Файловая Система",
                    Access.Levels.RightRead
            ).next("FileSystem");
            {
                //Категория FileSystem

                stateMachine.addPathProcessesMessages(
                        OperatorsWhoProcessesMessages::addCategory,
                        "AddCategory",
                        "Тут должно быть описание",
                        "Добавить Категорию",
                        Access.Levels.RightEdit
                );

                stateMachine.addPathGenerateKeyboard(
                        "ListCategories",
                        "Тут должно быть описание",
                        "Список категорий",
                        OperatorsWhichGeneratesKeyboard::listCategories,
                        MemberData.TypeReceivedInformation.IdCategory,
                        Access.Levels.RightEdit,
                        "Category",
                        "Тут должно быть описание",
                        Access.Levels.RightEdit
                ).next("ListCategories").next("Category");
                {
                    //Состояние Category

                    stateMachine.addPathRunAtStartup(
                            OperatorsWhichRunsAtStartup::deleteCategory,
                            "DeleteCategory",
                            "Удалить категорию",
                            Access.Levels.RightEdit
                    );

                    stateMachine.addPathProcessesMessages(
                            OperatorsWhoProcessesMessages::editCategory,
                            "EditCategory",
                            "Тут должно быть описание",
                            "Редактировать категорию",
                            Access.Levels.RightEdit
                    );

                    stateMachine.previous().previous();
                }

                stateMachine.addPathProcessesMessages(
                        OperatorsWhoProcessesMessages::addTitleFile,
                        "AddTitleFile",
                        "Тут должно быть описание",
                        null,
                        Access.Levels.RightEdit
                ).next("AddTitleFile");
                {
                    State relocatableState =
                        stateMachine.addPathProcessesMessages(
                                OperatorsWhoProcessesMessages::addFile,
                                "AddFile",
                                "Тут должно быть описание",
                                null,
                                Access.Levels.RightEdit
                        ).next("AddFile").getCurrentState();
                        stateMachine.previous();
                    stateMachine.getCurrentState().setStateNext(relocatableState);
                    stateMachine.deletePath(relocatableState);
                    stateMachine.previous();
                }
                stateMachine.relocationPathInPathGenerateKeyboard(
                        "ListCategoriesToAddFile",
                        "Тут должно быть описание",
                        "Добавить Файл",
                        OperatorsWhichGeneratesKeyboard::listCategories,
                        MemberData.TypeReceivedInformation.IdCategory,
                        Access.Levels.RightEdit,
                        "AddTitleFile"
                );

                stateMachine.addPath(
                        "Files",
                        "Тут должно быть описание",
                        "Файлы",
                        Access.Levels.RightRead
                ).next("Files");
                {

                    stateMachine.addPathGenerateKeyboard(
                            "ListFiles",
                            "Тут должно быть описание",
                            "Список Файлов",
                            OperatorsWhichGeneratesKeyboard::listFiles,
                            MemberData.TypeReceivedInformation.IdFile,
                            Access.Levels.RightRead,
                            "File",
                            "Тут должно быть описание",
                            Access.Levels.RightRead
                    ).next("ListFiles").next("File");
                    {
                        //вывод файла
                        stateMachine.getCurrentState().setOperatorWhichRunsAtStartup(OperatorsWhichRunsAtStartup::File);

                        stateMachine.addPathProcessesMessages(
                                OperatorsWhoProcessesMessages::editFile,
                                "EditFile",
                                "Тут должно быть описание",
                                "Изменить название файла",
                                Access.Levels.RightEdit
                        );

                        stateMachine.addPathRunAtStartup(
                                OperatorsWhichRunsAtStartup::deleteFile,
                                "DeleteFile",
                                "Удалить файл",
                                Access.Levels.RightEdit
                        );

                        stateMachine.previous().previous();
                    }

                    stateMachine.addPathGenerateKeyboard(
                            "ListFilesInCategory",
                            "Тут должно быть описание",
                            "Список Файлов",
                            OperatorsWhichGeneratesKeyboard::listFilesInCategory,
                            MemberData.TypeReceivedInformation.IdFile,
                            Access.Levels.RightRead,
                            "File",
                            "Тут должно быть описание",
                            Access.Levels.RightRead
                    ).next("ListFilesInCategory").next("File");
                    {
                        //вывод файла
                        stateMachine.getCurrentState().setOperatorWhichRunsAtStartup(OperatorsWhichRunsAtStartup::File);

                        stateMachine.addPathProcessesMessages(
                                OperatorsWhoProcessesMessages::editFile,
                                "EditFile",
                                "Тут должно быть описание",
                                "Изменить название файла",
                                Access.Levels.RightEdit
                        );

                        stateMachine.addPathRunAtStartup(
                                OperatorsWhichRunsAtStartup::deleteFile,
                                "DeleteFile",
                                "Удалить файл",
                                Access.Levels.RightEdit
                        );

                        stateMachine.previous().previous();
                    }
                    stateMachine.relocationPathInPathGenerateKeyboard(
                            "ListCategories",
                            "Тут должно быть описание",
                            "Выбрать категорию",
                            OperatorsWhichGeneratesKeyboard::listCategories,
                            MemberData.TypeReceivedInformation.IdCategory,
                            Access.Levels.RightRead,
                            "ListFilesInCategory");

                    stateMachine.addPathProcessesMessages(
                            OperatorsWhoProcessesMessages::searchFile,
                            "Search",
                            "поиск по всем файлам" +
                                    " в названии которых есть" +
                                    " такое сочетание букв",
                            "Поиск",
                            Access.Levels.RightRead
                    );

                    stateMachine.previous();
                }

                stateMachine.previous();
            }

            stateMachine.addPathRunAtStartup(
                    OperatorsWhichRunsAtStartup::DeleteGroup,
                    "DeleteGroup",
                    "Удалить группу",
                    Access.Levels.RightCreator
            );

            stateMachine.previous().previous();
        }


        return stateMachine.getDefaultState();
    }
}
