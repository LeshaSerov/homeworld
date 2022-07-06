package telegram;

import repository.Database;
import telegram.domain.MemberData;
import telegram.domain.State;
import telegram.domain.StateMachine;
import telegram.operators.OperatorsWhichGeneratesKeyboard;
import telegram.operators.OperatorsWhichRunsAtStartup;
import telegram.operators.OperatorsWhoProcessesMessages;
import util.ConnectionPool.ConnectionPool;

import java.util.function.BinaryOperator;

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

    public static State initializeDefaultState() {
        //Объявление машины состояний - тут главное описание.
        StateMachine stateMachine = new StateMachine(
                "Default",
                "Стандартное состояние"
        );

        stateMachine.addPathProcessesMessages(
                OperatorsWhoProcessesMessages::addGroup,
                "AddGroup",
                "Введите название группы",
                "Добавить Группу"
        );

        stateMachine.addPathGenerateKeyboard(
                "ListGroup",
                "Выберите группу из списка",
                "Список Групп",
                OperatorsWhichGeneratesKeyboard::listGroup,
                MemberData.TypeReceivedInformation.IdGroup,
                "Группа",
                "Группа"
        ).next("ListGroup").next("Group");
        {
            //Состояние Group

            stateMachine.addPathGenerateKeyboard(
                    "ListMembersGroup",
                    "Выберите участника из списка",
                    "Список участников группы",
                    OperatorsWhichGeneratesKeyboard::listMembersGroup,
                    MemberData.TypeReceivedInformation.IdMember,
                    "Member",
                    ""
            ).next("ListMembersGroup").next("Member");
            {
                //Состояние Member

                stateMachine.addPathRunAtStartup(
                        OperatorsWhichRunsAtStartup::deleteMember,
                        "DeleteMember",
                        "Удалить участника"
                );

                stateMachine.addPathRunAtStartup(
                        OperatorsWhichRunsAtStartup::changeRole,
                        "ChangeRole",
                        ""
                );
                stateMachine.relocationPathInPathGenerateKeyboard(
                        "ListRoles",
                        "",
                        "Изменить Роль",
                        OperatorsWhichGeneratesKeyboard::listRoles,
                        MemberData.TypeReceivedInformation.IdRole,
                        "ChangeRole"
                );

                stateMachine.previous().previous();
            }

            stateMachine.addPathRunAtStartup(
                    OperatorsWhichRunsAtStartup::addMember,
                    "AddMember",
                    ""
            );
            stateMachine.relocationPathInPathGenerateKeyboard(
                    "ListNonMembers",
                    "Нажмите на пользователя, чтобы добавить его в группу",
                    "Добавить участников в группу",
                    OperatorsWhichGeneratesKeyboard::listNonMembers,
                    MemberData.TypeReceivedInformation.IdMember,
                    "AddMember"
            );

            stateMachine.addPath(
                    "FileSystem",
                    "Работа с файлами",
                    "Файловая Система"
            ).next("FileSystem");
            {
                //Категория FileSystem

                stateMachine.addPathProcessesMessages(
                        OperatorsWhoProcessesMessages::addCategory,
                        "AddCategory",
                        "",
                        "Добавить Категорию"
                );

                stateMachine.addPathGenerateKeyboard(
                        "ListCategories",
                        "",
                        "Список категорий",
                        OperatorsWhichGeneratesKeyboard::listCategories,
                        MemberData.TypeReceivedInformation.IdCategory,
                        "Category",
                        ""
                ).next("ListCategories").next("Category");
                {
                    //Состояние Category

                    stateMachine.addPathRunAtStartup(
                            OperatorsWhichRunsAtStartup::deleteCategory,
                            "DeleteCategory",
                            "Удалить категорию"
                    );

                    stateMachine.addPathProcessesMessages(
                            OperatorsWhoProcessesMessages::editCategory,
                            "EditCategory",
                            "",
                            "Редактировать категорию"
                    );

                    stateMachine.previous().previous();
                }

                stateMachine.addPathProcessesMessages(
                        OperatorsWhoProcessesMessages::addFile,
                        "AddFile",
                        "",
                        null
                );
                stateMachine.relocationPathInPathGenerateKeyboard(
                        "ListCategories",
                        "",
                        "Добавить Файл",
                        OperatorsWhichGeneratesKeyboard::listCategories,
                        MemberData.TypeReceivedInformation.IdCategory,
                        "AddFile"
                );

//                stateMachine.addPathGenerateKeyboard(
//
//                );
//
//
//                stateMachine.addPathGenerateKeyboard(
//                        "ListFiles",
//                        "",
//                        "Списки файлов",
//                        operator,
//                        "ListCategories",
//                        ""
//                )
            }

            stateMachine.previous().previous();
        }

        return stateMachine.getDefaultState();
    }
}
