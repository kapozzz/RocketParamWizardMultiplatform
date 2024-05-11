package core.solvers

import core.models.Fuel
import core.models.ProjectParams
import core.objects.Constants
import core.objects.DependentConstants
import kotlin.math.cbrt
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin


class VerificationBallisticCalculation(
    private val projectParams: ProjectParams,
    private val fuel: Fuel,
    private val designBallisticCalculation: DesignBallisticCalculation,
    private val determinationOfEngineEfficiencyIndicators: DeterminationOfEngineEfficiencyIndicators
) {

    /**
     * Число Циолковского для первой ступени
     */
    val cValueFirstStage by lazy {
        ln(1 / (1 - designBallisticCalculation.reducedPropellantFillFactorForFirstStage))
    }

    /**
     * Число Циолковского для второй ступени
     */
    val cValueSecondStage by lazy {
        ln(1 / (1 - designBallisticCalculation.reducedPropellantFillFactorForSecondStage))
    }

    /**
     *  Потери скорости на преодоление гравитационных сил
     */
    val velocityLessOnGravitationPowers by lazy {
        DependentConstants.getIg(
            designBallisticCalculation.reducedPropellantFillFactorForFirstStage,
            designBallisticCalculation.dependenciesParameters.angle
        )
    }

    /**
     * Потери скорости на преодоление лобового сопротивления
     */
    val velocityLessOnFrontPowers by lazy {
        (DependentConstants.getIx1(designBallisticCalculation.reducedPropellantFillFactorForFirstStage) /
                (projectParams.initialThrustCapacityOfTheRocket.first * cbrt(
                    sin(
                        designBallisticCalculation.angleInRadians
                    ).pow(2)
                ))) *
                (Constants.GREAT_MID_LOAD / projectParams.initialTransverseLoadOnTheMidsection)
    }

    /**
     * Уравнение скорости в проекции на оси скоростной (поточной) системы координат после интегрирования по весу выгоревшего топлива µк1
     */
    val firstVelocityEqualization by lazy {
        Constants.FREE_FALL_ACCELERATION * determinationOfEngineEfficiencyIndicators.specificGravityInVoidFirst * cValueFirstStage -
                Constants.FREE_FALL_ACCELERATION * projectParams.initialThrustCapacityOfTheRocket.first * determinationOfEngineEfficiencyIndicators.specificGravityOnStartFromEarth *
                DependentConstants.getIg(
                    designBallisticCalculation.reducedPropellantFillFactorForFirstStage,
                    designBallisticCalculation.dependenciesParameters.angle
                ) - determinationOfEngineEfficiencyIndicators.specificGravityOnStartFromEarth * DependentConstants.getIp1(
            designBallisticCalculation.reducedPropellantFillFactorForFirstStage
        ) - velocityLessOnFrontPowers
    }


}