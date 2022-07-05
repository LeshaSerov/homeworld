package telegram;

import repository.Database;
import telegram.domain.MemberData;
import telegram.domain.State;
import telegram.domain.StateMachine;
import telegram.operators.OperatorsWhichGeneratesKeyboard;
import util.ConnectionPool.ConnectionPool;

import java.util.function.BinaryOperator;

public class Initiator {

    public static Boolean reset(ConnectionPool connector) {
        try {
            return Database.recreateDatabase(connector) && Database.createAllRoles(connector) && Database.createResourceChat(connector);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static State initializeDefaultState() {

        //Объявление машины состояний - тут главное описание.
        StateMachine stateMachine = new StateMachine(
                "Default",
                "Привет)))"
        );

        stateMachine.addPathProcessesMessages(
                operator,
                "AddGroup",
                "Введите название группы",
                "Добавить Группу"
        );

        stateMachine.addPathGenerateKeyboard(
                "ListGroup",
                "",
                "Список Групп",
                OperatorsWhichGeneratesKeyboard::listGroup,
                MemberData.TypeReceivedInformation.IdGroup,
                "Группа",
                ""
        ).next("ListGroup").next("Group");
        {
            //Состояние Group

            stateMachine.addPathGenerateKeyboard(
                    "ListMembersGroup",
                    "",
                    "Список участников группы",
                    OperatorsWhichGeneratesKeyboard::listMembersGroup,
                    MemberData.TypeReceivedInformation.IdMember,
                    "Member",
                    ""
            ).next("ListMembersGroup").next("Member");
            {
                //Состояние Member

                stateMachine.addPathRunAtStartup(
                        operator,
                        "DeleteMember",
                        "Удалить участника"
                );

                stateMachine.addPathRunAtStartup(
                        operator,
                        "ChangeRole",
                        ""
                );
                stateMachine.relocationPathInPathGenerateKeyboard(
                        "ListRoles",
                        "",
                        "Изменить Роль",
                        operator,
                        MemberData.TypeReceivedInformation.IdRole,
                        "ChangeRole"
                );

                stateMachine.previous().previous();
            }

            stateMachine.addPathRunAtStartup(
                    operator,
                    "AddMember",
                    ""
            );
            stateMachine.relocationPathInPathGenerateKeyboard(
                    "ListNonMembers",
                    "",
                    "Добавить участников в группу",
                    operator,
                    MemberData.TypeReceivedInformation.IdMember,
                    "AddMember"
            );

            stateMachine.addPath(
                    "FileSystem",
                    "",
                    "Файловая Система"
            ).next("FileSystem");
            {
                //Категория FileSystem

                stateMachine.addPathProcessesMessages(
                        operator,
                        "AddCategory",
                        "",
                        "Добавить Категорию"
                );

                stateMachine.addPathGenerateKeyboard(
                        "ListCategories",
                        "",
                        "Список категорий",
                        operator,
                        MemberData.TypeReceivedInformation.IdCategory,
                        "Category",
                        ""
                ).next("ListCategories").next("Category");
                {
                    //Состояние Category

                    stateMachine.addPathRunAtStartup(
                            operator,
                            "DeleteCategory",
                            "Удалить категорию"
                    );

                    stateMachine.addPathProcessesMessages(
                            operator,
                            "EditCategory",
                            "",
                            "Редактировать категорию"
                    );

                    stateMachine.previous().previous();
                }

                stateMachine.addPathProcessesMessages(
                        operator,
                        "AddFile",
                        "",
                        null
                );
                stateMachine.relocationPathInPathGenerateKeyboard(
                        "ListCategories",
                        "",
                        "Добавить Файл",
                        operator,
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
