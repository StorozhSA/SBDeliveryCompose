package ru.skillbranch.common.validation

import android.util.Patterns

public fun interface IValidator<T> {
    public fun validate(value: T): Boolean
}

public interface IValidatorAggregated<T, M> {
    public fun validate(value: T, hasError: (msg: M) -> Unit): Boolean
}

public class StringValidators {

    public companion object {

        public val isRUorEN: IValidator<String> = IValidator {
            it.matches("(^[a-zA-Z]*\$|^[а-яА-ЯЁё]*\$)".toRegex())
        }

        public val isEmpty: IValidator<String> = IValidator {
            it.isBlank()
        }

        public val isNotEmpty: IValidator<String> = IValidator {
            it.isNotBlank()
        }

        public val isEmail: IValidator<String> = IValidator {
            Patterns.EMAIL_ADDRESS.matcher(it).matches()
        }

        public val hasDigits: IValidator<String> = IValidator {
            it.matches(".*\\d+.*".toRegex())
        }

        public fun minLength(min: Int): IValidator<String> = IValidator {
            it.length > min
        }

        public fun isEquals(value: String): IValidator<String> = IValidator {
            it == value
        }

    }
}

public class ValidatorAggregated<T>(
    vararg elements: Pair<IValidator<T>, String>
) : IValidatorAggregated<T, String> {
    private val validatorsList = if (elements.isNotEmpty()) elements.asList() else emptyList()
    public override fun validate(value: T, hasError: (msg: String) -> Unit): Boolean {
        var result = true
        validatorsList.forEach {
            if (!it.first.validate(value)) {
                hasError.invoke(it.second)
                result = false
                return@forEach
            }
        }
        return result
    }
}

public class ValidatorAggregatedByStringRes<T>(
    vararg elements: Pair<IValidator<T>, Int>
) : IValidatorAggregated<T, Int> {
    private val validatorsList = if (elements.isNotEmpty()) elements.asList() else emptyList()
    public override fun validate(value: T, hasError: (msg: Int) -> Unit): Boolean {
        var result = true
        validatorsList.forEach {
            if (!it.first.validate(value)) {
                hasError.invoke(it.second)
                result = false
                return@forEach
            }
        }
        return result
    }
}

