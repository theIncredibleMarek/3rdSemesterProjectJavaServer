$(document).ready(function () {
    fetchAll();
    deletePerson();
    initPersons();
    initPersons2();
    initAddBtn();
    initEditBtn();
    initCancelBtn();
    initSaveBtn();
    initAddRoleBtn();
    initDetails(false);
    $("#details2").html("");
});

var addingNewRole = false;

function initAddBtn() {
    $("#btn_add").click(function () {
        initDetails(true);
        $("#details2").html("");
        fetchAll();
        $("#fname").focus();
        $("#persons").attr("disabled", "disabled");
    });
}

function initAddRoleBtn() {
    $("#btn_addrole").click(function () {
        if ($("#details2").find('label').length >= 3)
        {
            alert("A person can have up to 3 roles!");
        } else
        {
            addingNewRole = true;
            initDetails(false);
            $("#btn_add").attr("disabled", "disabled");
            $("#newRole").removeAttr("hidden");
            $("#btn_save").removeAttr("disabled");
            $("#btn_cancel").removeAttr("disabled");
        }
    });
}

function initSaveBtn() {
    $("#btn_save").click(function () {
        if ($("#fname").val() !== "" && $("#lname").val() !== "" && $("#email").val() !== "" && $("#phone").val() !== "")
        {
            var newPerson;
            var requestType;
            if ($("#id").val() === "")
            {
                newPerson = {"firstName": $("#fname").val(), "lastName": $("#lname").val(), "email": $("#email").val(), "phone": $("#phone").val()};
                requestType = "post";
            }
            else
            {
                newPerson = {"id": $("#id").val(), "firstName": $("#fname").val(), "lastName": $("#lname").val(), "email": $("#email").val(), "phone": $("#phone").val()};
                requestType = "put";
            }

            if (!addingNewRole) {
                $.ajax({
                    url: "../person",
                    data: JSON.stringify(newPerson),
                    type: requestType,
                    dataType: 'json',
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert(jqXHR.responseText + ": " + textStatus);
                    }
                }).done(function (newPerson) {
                    $("#id").val(newPerson.id);
                    initDetails(false);
                    fetchAll();
                    $("#persons").removeAttr("disabled");
                    $("#details2").html("");
                    $("#newRole").attr("hidden", "hidden");
                });
            }
            else
            {
                requestType = "post";
                var newRole;
//                helper:
//                $('#docsList').get(0).selectedIndex = 0;
//                $('#docsList').val('0');
                if ($("#newRoleSelectBox").get(0).selectedIndex === 0)
                {
                    newRole = {"semester": $("#roleAttribute").val(), "roleName": "Student"};
                } else if ($("#newRoleSelectBox").get(0).selectedIndex === 1)
                {
                    newRole = {"degree": $("#roleAttribute").val(), "roleName": "Teacher"};
                } else {
                    newRole = {"roleName": "AssistantTeacher"};
                }
                $.ajax({
                    url: "../role/" + $("#id").val(),
                    data: JSON.stringify(newRole),
                    type: requestType,
                    dataType: 'json',
                    error: function (jqXHR, textStatus, errorThrown) {
//                        alert(jqXHR.responseText + ": " + textStatus);
                    }
                }).done(function () {
                    initDetails(false);
                    fetchAll();
                    $("#persons").removeAttr("disabled");
                    $("#details2").html("");
                    $("#newRole").attr("hidden", "hidden");
                });
            }
            addingNewRole = false;
        }
        else
        {
            alert("You must complete all the fields before saving!");
        }
    });
}

function initCancelBtn() {
    $("#btn_cancel").click(function () {
        if ($("#id").val() === "")
        {
            clearDetails();
        }
        else
        {
            updateDetails($("#id").val());
        }
        initDetails(false);
        addingNewRole = false;
        $("#persons").removeAttr("disabled");
        $("#newRole").attr("hidden", "hidden");
    });
}

function initPersons() {
    $("#persons").click(function (e) {
        var id = e.target.id;
        if (isNaN(id)) {
            return;
        }
        updateDetails(id);
    });
}

function initPersons2() {
    $("#persons").keyup(function (e) {
        if (e.keyCode === 13) {
            alert('you pressed enter ^_^');
        }
        if (e.keyCode === 38 || e.keyCode === 40)
        {
            var id = e.target.options.item(e.target.options.selectedIndex).id;
            if (isNaN(id)) {
                return;
            }
            updateDetails(id);
        }
    });
}

function deletePerson() {
    $("#btn_delete").click(function () {
        $.ajax({
            url: "../person/" + $("#persons option:selected").attr("id"),
            type: "DELETE",
            dataType: 'json',
            error: function (jqXHR, textStatus, errorThrown) {
                alert(jqXHR.responseText + ": " + textStatus);
            }
        }).done(function () {
            fetchAll();
            initDetails(false);
            $("#details2").html("");
        });
    });
}

function initEditBtn() {
    $("#btn_edit").click(function () {
        initDetails(true);
        $("#persons").attr("disabled", "disabled");
    });
}

function initDetails(init) {
    if (init) {
        $("#fname").removeAttr("disabled");
        $("#lname").removeAttr("disabled");
        $("#email").removeAttr("disabled");
        $("#phone").removeAttr("disabled");
        $("#btn_add").attr("disabled", "disabled");
        $("#btn_edit").attr("disabled", "disabled");
        $("#btn_save").removeAttr("disabled");
        $("#btn_cancel").removeAttr("disabled");
        $("#btn_delete").attr("disabled", "disabled");
        $("#btn_addrole").attr("disabled", "disabled");
    }
    else {
        $("#fname").attr("disabled", "disabled");
        $("#lname").attr("disabled", "disabled");
        $("#email").attr("disabled", "dsiabled");
        $("#phone").attr("disabled", "dsiabled");
        $("#btn_add").removeAttr("disabled");
        $("#btn_edit").attr("disabled", "disabled");
        $("#btn_save").attr("disabled", "disabled");
        $("#btn_cancel").attr("disabled", "disabled");
        $("#btn_delete").attr("disabled", "disabled");
        $("#btn_addrole").attr("disabled", "disabled");
    }
}

function clearDetails() {
    $("#id").val("");
    $("#fname").val("");
    $("#lname").val("");
    $("#email").val("");
    $("#phone").val("");
}

function updateDetails(id) {
    $.ajax({
        url: "../person/" + id,
        type: "GET",
        dataType: 'json',
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.getResonseText + ": " + textStatus);
        }
    }).done(function (person) {
        $("#id").val(person.id);
        $("#fname").val(person.firstName);
        $("#lname").val(person.lastName);
        $("#email").val(person.email);
        $("#phone").val(person.phone);
        $("#btn_delete").removeAttr("disabled");
        $("#btn_addrole").removeAttr("disabled");
        $("#btn_edit").removeAttr("disabled");
        var details_containers = "";
        var numberOfRoles = 0;
        person.roles.forEach(function (role) {
            numberOfRoles++;
            details_containers += "<div><label>Role #" + numberOfRoles + "</label><input id=\"role" + numberOfRoles + "\" value=\"" + role.roleName + "\" disabled=\"disabled\" type=\"text\" size=\"27\"/></div>";
        });
        $("#details2").html(details_containers);
    });
}

function fetchAll() {
    $.ajax({
        url: "../person",
        type: "GET",
        dataType: 'json',
        error: function (jqXHR, textStatus, errorThrown) {
            alert(textStatus);
        }
    }).done(function (persons) {
        var options = "";
        persons.forEach(function (person) {
            options += "<option id=" + person.id + ">" + person.firstName + ", " + person.lastName + "</option>";
        });
        $("#persons").html(options);
        clearDetails();
    });
}