<%--
  Created by IntelliJ IDEA.
  User: ilal
  Date: 11/07/25
  Time: 17.35
--%>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="warkop"/>
    <title>Papan Kasir</title>
</head>
<body>
<g:if test="${flash.message}">
    <div class="alert alert-success" role="alert">
        ${flash.message}
    </div>
</g:if>

<div class="card">
    <div class="card-header">
        <h3>Buat Pesanan Baru</h3>
    </div>
    <div class="card-body">
        <g:form controller="order" action="createOrder">
            <table class="table table-hover">
                <thead class="table-light">
                <tr>
                    <th>Nama Item</th>
                    <th>Kategori</th>
                    <th>Harga</th>
                    <th style="width: 15%;">Kuantitas</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${menuItems}" var="item">
                    <tr>
                        <td>${item.name}</td>
                        <td><span class="badge ${item.category == 'DRINK' ? 'bg-primary' : 'bg-warning text-dark'}">${item.category}</span></td>
                        <td><g:formatNumber number="${item.price}" type="currency" currencyCode="IDR"/></td>
                        <td>
                            <g:textField name="item.${item.id}" type="number" min="0" value="0" class="form-control form-control-sm"/>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
            <hr/>
            <g:submitButton name="placeOrder" value="Buat Pesanan" class="btn btn-primary w-100"/>
        </g:form>
    </div>
</div>
</body>
</html>