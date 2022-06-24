package telegram;

import repository.Database;
import telegram.domain.State;
import util.ConnectionPool.ConnectionPool;

public class Initiator {

    public Boolean initializeDatabase(ConnectionPool connector){
        try{
            return Database.recreateDatabase(connector) && Database.createAllRoles(connector);
        }catch (Exception ignored)
        {
            return false;
        }
    }

    public State initializeDefaultState() {
        String description = "Default";
        State aDefault = new State("Default", description);

        description = "AddGroup";
        aDefault.addPath(new State("AddGroup", description, "Добавить Группу", aDefault));
        aDefault.next("AddGroup").setHandlerMessage("AddGroup");

        description = "ListGroup";
        aDefault.addPath(new State("ListGroup", description, "Список Групп", aDefault));
        aDefault = aDefault.next("ListGroup");
        {
            description = "Group";
            aDefault.addButtonGenerating("ListGroup", new State("Group", description, null, aDefault));
            aDefault = aDefault.next("Group");
            {
                description = "ListMembersGroup";
                aDefault.addPath(new State("ListMembersGroup", description, "Список участников группы", aDefault));
                aDefault = aDefault.next("ListMembersGroup");
                {
                    description = "Member";
                    aDefault.addButtonGenerating("ListMembersGroup", new State("Member", description, null, aDefault));
                    aDefault = aDefault.next("Member");
                    {
                        description = null;
                        aDefault.addPath(new State("DeleteMember", description, "Удалить участника", aDefault.previous()));
                        aDefault.next("DeleteMember").setHandlerActivator("DeleteMEmber");

                        description = "ListRoles";
                        aDefault.addPath(new State("ListRoles", description, "Изменить роль участника", aDefault));
                        aDefault = aDefault.next("ListRoles");
                        {
                            description = "ChangeRole";
                            aDefault.addButtonGenerating("ListRoles", new State("ChangeRole", description, null, aDefault));
                            aDefault.next("ChangeRole").setHandlerMessage("ChangeRole");

                            aDefault = aDefault.previous();
                        }
                        aDefault = aDefault.previous();
                    }
                    aDefault = aDefault.previous();
                }

                description = "ListOfApplicants";
                aDefault.addPath(new State("ListOfApplicants", description, "Добавить участника в группу", aDefault));
                aDefault = aDefault.next("ListOfApplicants");
                {
                    description = null;
                    aDefault.addButtonGenerating("ListOfApplicants", new State("AddMember", description, null, aDefault));
                    aDefault.next("AddMember").setHandlerActivator("AddMember");

                    aDefault = aDefault.previous();
                }

                description = "FileSystem";
                aDefault.addPath(new State("FileSystem", description, "Файловая Система", aDefault));
                aDefault = aDefault.next("FileSystem");
                {
                    description = "AddCategory";
                    aDefault.addPath(new State("AddCategory", description, "Добавить категорию", aDefault));
                    aDefault.next("AddCategory").setHandlerMessage("AddCategory");

                    description = "ListCategories";
                    aDefault.addPath(new State("ListCategories", description, "Список Категорий", aDefault));
                    aDefault = aDefault.next("ListCategories");
                    {
                        description = "Category";
                        aDefault.addButtonGenerating("ListCategories", new State("Category", description, null, aDefault));
                        aDefault = aDefault.next("Category");
                        {
                            description = null;
                            aDefault.addPath(new State("DeleteCategory", description, "Удалить категорию", aDefault.previous()));
                            aDefault.next("DeleteCategory").setHandlerActivator("DeleteCategory");

                            description = "EditCategory";
                            aDefault.addPath(new State("EditCategory", description, "Редактировать категорию", aDefault));
                            aDefault.next("EditCategory").setHandlerMessage("EditCategory");

                            aDefault = aDefault.previous();
                        }
                        aDefault = aDefault.previous();
                    }

                    description = "SelectCategory";
                    aDefault.addPath(new State("SelectCategory", description, "Добавление файла", aDefault));
                    aDefault = aDefault.next("SelectCategory");
                    {
                        description = "AddFile";
                        aDefault.addButtonGenerating("ListCategories", new State("AddFile", description, null, aDefault));
                        aDefault.next("AddFile").setHandlerMessage("AddFile");

                        aDefault = aDefault.previous();
                    }

                    description = "SelectListFiles";
                    aDefault.addPath(new State("SelectListFiles", description, "Файлы", aDefault));
                    aDefault = aDefault.next("SelectListFiles");
                    {
                        description = "SelectCategory";
                        aDefault.addPath(new State("SelectCategory", description, null, aDefault));
                        aDefault = aDefault.next("SelectCategory");
                        {
                            description = "ListCategories";
                            aDefault.addButtonGenerating("ListCategories", new State("SelectDate", description, null, aDefault));

                            description = null;
                            aDefault = aDefault.next("SelectDate");
                            {
                                aDefault.addButtonGenerating("ListDates", new State("GetFiles", description, null, aDefault.previous().previous().previous()));
                                aDefault.next("GetFiles").setHandlerActivator("GetFiles");
                                aDefault = aDefault.previous();
                            }
                            aDefault = aDefault.previous();
                        }
                        aDefault = aDefault.previous();
                    }
                    aDefault = aDefault.previous();
                }
                aDefault = aDefault.previous();
            }
            aDefault = aDefault.previous();
        }
        aDefault = aDefault.previous();
        return aDefault;
    }
}
