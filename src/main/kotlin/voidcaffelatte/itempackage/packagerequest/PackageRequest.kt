package voidcaffelatte.itempackage.packagerequest

interface PackageRequest
{
    fun generateAsync(onSucceeded: () -> Unit, onFailed: () -> Unit)
}