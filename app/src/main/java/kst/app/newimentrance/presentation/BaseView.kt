package kst.app.newimentrance.presentation

interface BaseView<PresenterT : BasePresenter> {

    val presenter: PresenterT
}